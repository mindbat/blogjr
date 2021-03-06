(ns blogjr.db
  (:use clojure.contrib.sql))

(def *db*
  {:subprotocol "postgresql" 
   :subname "//localhost:5432/blogjr"
   :username "blogjr"
   :password ""
   :classname "org.postgresql.jdbcDriver"})

(defn create-posts []
  (with-connection *db*
    (create-table :posts
      [:id "SERIAL" "PRIMARY KEY"]
      [:title :varchar "NOT NULL"]
      [:body :varchar "NOT NULL"]
      [:created_at :timestamp])))

(defn now [] 
  (java.sql.Timestamp. 
    (.getTime (java.util.Date.))))

(defn insert-sample-posts []
  (let [timestamp (now)]
    (seq
      (with-connection *db*
        (insert-values :posts
          [:title :body :created_at]
          ["First Post" "This is your first post." timestamp]
          ["Second Post" "Your second post is longer than the first." timestamp])))))

(defn select-posts []
  (seq
    (with-connection *db*
      (with-query-results res ["select * from posts order by id desc"] (doall res)))))

(defn sql-query [q]
  (with-query-results res q (doall res)))

(defn last-created-id
  "Extract the last created id. Must be called in a transaction
   that performed an insert. Expects PostgreSQL return structure of
   the form [{@p0 id}]."
  []
  (first
    (vals
      (first 
        (sql-query ["SELECT lastval();"])))))

(defn insert-post [title body]
  (with-connection *db*
    (transaction
      (insert-values :posts
        [:title :body :created_at]
        [title body (now)])
      (last-created-id))))

(defn post-update [id title body]
  (with-connection *db*
    (transaction
      (update-values :posts
        ["id=?" id]
        {:title title :body body}))))

(defn select-post [id]
  (first
    (with-connection *db*
      (sql-query [(str "select * from posts where id = " id)]))))

(defn create-users []
  (with-connection *db*
    (create-table :users
      [:id "SERIAL" "PRIMARY KEY"]
      [:name :varchar "NOT NULL"]
      [:pass :varchar "NOT NULL"]
      [:created_at :timestamp]
      [:last_login :timestamp])))

(defn insert-user [name pass]
  (with-connection *db*
    (transaction
      (insert-values :users
        [:name :pass :created_at]
        [name pass (now)])
      (last-created-id))))

(defn is-user? [name pass]
  (let [id 
    (first
      (with-connection *db*
        (sql-query [(str "select id from users where name = '" name "' and pass = '" pass "'")])))]
    (if id true false)))

(defn delete-post [id]
  (with-connection *db*
    (transaction
      (delete-rows :posts ["id=?" id]))))

(defn create-comments []
  (with-connection *db*
    (create-table :comments
      [:id "SERIAL" "PRIMARY KEY"]
      [:post_id :int "NOT NULL"]
      [:body :varchar "NOT NULL"]
      [:created_at :timestamp]
      [:author :varchar "NOT NULL"]
      ["FOREIGN KEY" "(post_id)" "REFERENCES" :posts "(id)"])))

(defn insert-comment [post_id body author]
  (with-connection *db*
    (transaction
      (insert-values :comments
        [:post_id :body :author :created_at]
        [post_id body author (now)])
      (last-created-id))))

(defn select-comments [post_id]
  (with-connection *db*
    (sql-query [(str "select * from comments where post_id = " post_id)])))
