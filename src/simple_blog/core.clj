(ns simple-blog.core
  (:use compojure.core, ring.adapter.jetty, hiccup.core, hiccup.form-helpers, hiccup.page-helpers, simple-blog.db, simple-blog.view)
  (:require [compojure.route :as route])
)

; Function to create a form for entering blog posts
(defn new-post []
  (layout "Create New Post"
    (html
      (form-to {:class "new-post"} [:post "/post/submit"]
        [:div {:class "form-item"}
          (label "name" "Username")
          (text-field "name")
        ]
        [:div {:class "form-item"}
          (label "pass" "Password")
          (password-field "pass")
        ]
        [:div {:class "form-item"}
          (label "title" "Post Title")
          (text-field "title")
        ]
        [:div {:class "form-item"}
          (label "body" "Post")
          (text-area {:rows 5 :cols 26} "body")
        ]
        [:div {:class "form-buttons"}
          (submit-button "Save")
          [:p {:class "inline"} [:a {:href "/"} "Cancel"]]
        ]
      )
    )
  )
)

; Function to display all posts 
(defn display-posts []
  (layout "Simple Blog" 
    (html
      (for [post (select-posts)]
        (html-post post)
      )
    )
  )
)

; Function to handle creation of blog post on postback
(defn create-post [name pass title body]
  (if (is-user? name pass)
    (if-let [id (insert-post title body)]
      {:status 302 :headers {"Location" (str "/post/" id)}}
      {:status 302 :headers {"Location" "/" }}
    )
    {:status 302 :headers {"Location" "/"}}
  )
)

; Function to handle update of blog post on postback
(defn update-post [id name pass title body]
  (if (is-user? name pass)
    (if (post-update id title body)
      {:status 302 :headers {"Location" (str "/post/" id)}}
      {:status 302 :headers {"Location" "/" }}
    )
    {:status 302 :headers {"Location" "/"}}
  )
)

; Function to display a single blog post
(defn show-post [id]
  (let [post (select-post id)]
    (layout (:title post) (html-post post))
  )
)

; Function to create a form for editing blog posts
(defn edit-post [id]
  (let [post (select-post id)]
    (layout "Edit Post"
      (html
        (form-to {:class "update-post"} [:post "/post/update"]
          [:div {:class "form-item"}
            (hidden-field "id" id)
          ]
          [:div {:class "form-item"}
            (label "name" "Username")
            (text-field "name")
          ]
          [:div {:class "form-item"}
            (label "pass" "Password")
            (password-field "pass")
          ]
          [:div {:class "form-item"}
            (label "title" "Post Title")
            (text-field "title" (:title post))
          ]
          [:div {:class "form-item"}
            (label "body" "Post")
            (text-area {:rows 5 :cols 26} "body" (:body post))
          ]
          [:div {:class "form-buttons"}
            (submit-button "Save")
            [:p {:class "inline"} [:a {:href "/"} "Cancel"]]
          ]
        )
      )
    )
  )
)

; Url handlers for viewing single blog posts, viewing all blog posts, and entering blog posts
(defroutes blog-app
  "Create and view blog posts"
  (GET "/post/new" [] (new-post))
  (GET "/post/edit/:id" [id] (edit-post id))
  (GET "/post/:id" [id] (show-post id))
  (POST "/post/submit" [name pass title body] (create-post name pass title body))
  (POST "/post/update" [id name pass title body] (update-post id name pass title body))
  (route/files "/" {:root "public"})
  (GET "/" [] (display-posts))
  (route/not-found "Page is not here, no matter how hard we search") 
)
