(ns blogjr.core
  (:use compojure.core, hiccup.core, hiccup.form-helpers, hiccup.page-helpers)
  (:require [compojure.route :as route]
            [compojure.handler :as handler]
            [blogjr.db :as db]
            [blogjr.view :as view]))

; Function to create a form for entering blog posts
(defn new-post []
  (view/layout "Create New Post"
    (html
      (form-to {:class "new-post"} [:post "/post/submit"]
        [:div {:class "form-item"}
          (label "name" "Username")
          (text-field "name")]
        [:div {:class "form-item"}
          (label "pass" "Password")
          (password-field "pass")]
        [:div {:class "form-item"}
          (label "title" "Post Title")
          (text-field "title")]
        [:div {:class "form-item"}
          (label "body" "Post")
          (text-area {:rows 10 :cols 58} "body")]
        [:div {:class "form-buttons"}
          (submit-button "Save")
          [:p {:class "inline"} [:a {:href "/"} "Cancel"]]]))))

; Function to display all posts 
(defn display-posts []
  (view/layout "Blogjr" 
    (html
      (for [post (db/select-posts)]
        (view/html-post post)))))

; Function to handle creation of blog post on postback
(defn create-post [name pass title body]
  (if (db/is-user? name pass)
    (if-let [id (db/insert-post title body)]
      {:status 302 :headers {"Location" (str "/post/" id)}}
      {:status 302 :headers {"Location" "/" }})
    {:status 302 :headers {"Location" "/"}}))

; Function to handle update of blog post on postback
(defn update-post [id name pass title body]
  (if (db/is-user? name pass)
    (if (db/post-update id title body)
      {:status 302 :headers {"Location" (str "/post/" id)}}
      {:status 302 :headers {"Location" "/" }})
    {:status 302 :headers {"Location" "/"}}))

; Function to display a single blog post
(defn show-post [id]
  (let [post (db/select-post id)]
    (view/layout 
      (:title post) 
      (html
        (view/html-post post)
        (view/post-actions (:id post))))))

; Function to create a form for editing blog posts
(defn edit-post [id]
  (let [post (db/select-post id)]
    (view/layout "Edit Post"
      (html
        (form-to {:class "update-post"} [:post "/post/update"]
          [:div {:class "form-item"}
            (hidden-field "id" id)]
          [:div {:class "form-item"}
            (label "name" "Username")
            (text-field "name")]
          [:div {:class "form-item"}
            (label "pass" "Password")
            (password-field "pass")]
          [:div {:class "form-item"}
            (label "title" "Post Title")
            (text-field "title" (:title post))]
          [:div {:class "form-item"}
            (label "body" "Post")
            (text-area {:rows 10 :cols 58} "body" (:body post))]
          [:div {:class "form-buttons"}
            (submit-button "Save")
            [:p {:class "inline"} [:a {:href "/"} "Cancel"]]])))))

; Function to delete a post
(defn post-delete [id]
  (if (db/delete-post id)
    {:status 302 :headers {"Location" "/" }}
    {:status 302 :headers {"Location" (str "/post/" id)}}))

; Url handlers for viewing single blog posts, viewing all blog posts, and entering blog posts
(defroutes main-routes 
  "Create and view blog posts"
  (GET "/post/new" [] (new-post))
  (GET "/post/edit/:id" [id] (edit-post id))
  (GET "/post/:id" [id] (show-post id))
  (POST "/post/submit" [name pass title body] (create-post name pass title body))
  (POST "/post/update" [id name pass title body] (update-post id name pass title body))
  (GET "/post/delete/:id" [id] (post-delete id))
  (GET "/" [] (display-posts))
  (route/resources "/")
  (route/not-found "Page is not here, no matter how hard we search"))

; Add the route handlers to the site
(def app
  (handler/site main-routes))
