(ns simple-blog.db
  (:use clojure.contrib.sql)
)

(def db {:classname "org.hsqldb.jdbcDriver"
         :subprotocol "hsqldb"
         :subname "file:/var/tmp/simpleblog-db"
        }
)

(defn create-posts []
  (create-table :posts
    [:id :int "IDENTITY" "PRIMARY KEY"]
    [:title :varchar "NOT NULL"]
    [:body :varchar "NOT NULL"]
    [:created_at :datetime]
  )
)

(defn now [] 
  (java.sql.Timestamp. 
    (.getTime (java.util.Date.)
    )
  )
)

(defn insert-sample-posts []
  (let [timestamp (now)]
    (seq
      (insert-values :posts
        [:title :body :created_at]
        ["First Post" "This is your first post." timestamp]
        ["Second Post" "Your second post is longer than the first." timestamp]
      )
    )
  )
)

(defn select-posts []
  (seq
    (with-connection db
      (with-query-results res ["select * from posts order by id desc"] (doall res)
      )
    )
  )
)

(defn sql-query [q]
  (with-query-results res q (doall res)
  )
)

(defn last-created-id
  "Extract the last created id. Must be called in a transaction
   that performed an insert. Expects HSQLDB return structure of
   the form [{@p0 id}]."
  []
  (first
    (vals
      (first 
        (sql-query ["CALL IDENTITY()"])
      )
    )
  )
)

(defn insert-post [title body]
  (with-connection db
    (transaction
      (insert-values :posts
        [:title :body :created_at]
        [title body (now)]
      )
      (last-created-id)
    )
  )
)

(defn select-post [id]
  (first
    (with-connection db
      (sql-query [(str "select * from posts where id = " id)])
    )
  )
)
