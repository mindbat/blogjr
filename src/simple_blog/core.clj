(ns simple-blog.core
  (:use compojure.core, ring.adapter.jetty, hiccup.core, hiccup.form-helpers, hiccup.page-helpers, simple-blog.db)
  (:require [compojure.route :as route])
)

; Function to create a form for entering blog posts
(defn new-post []
  (html
    (form-to [:post "/post/submit"]
      (label "title" "Post Title")
      [:br]
      (text-field "title")
      [:br]
      (label "body" "Post")
      [:br]
      (text-area {:rows 5 :cols 26} "body")
      [:br]
      (submit-button "Save")
    )
    [:p [:a {:href "/"} "Cancel"]]
  )
)

; Setup general page layout and include any js and css files we need
(defn layout [title body]
  (html
    [:head 
      [:title title]
      (include-css "/css/blog.css")
    ]
    [:body
      [:h1 
        [:a {:href "/"} "Simple Blog"]
      ]
      body
      [:p [:a {:href "/post/new"} "New post"]]
    ] 
  )
)

; Function to convert a post to html
(defn html-post [post]
  (html
    [:div {:class "post"}
      [:h2
        [:a
          {:href (str "/post/" (:id post))} 
          (:title post)
        ]
      ]
      [:p (:body post)]
      [:div {:class "date"} (:created_at post)]
    ]
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
(defn create-post [title body]
  (if-let [id (insert-post title body)]
    {:status 302 :headers {"Location" (str "/post/" id)}}
    {:status 302 :headers {"Location" "/" }}
  )
)

; Function to display a single blog post
(defn show-post [id]
  (let [post (select-post id)]
    (layout (:title post) (html-post post))
  )
)

; Url handlers for viewing single blog posts, viewing all blog posts, and entering blog posts
(defroutes blog-app
  "Create and view blog posts"
  (GET "/post/new" [] (new-post))
  (GET "/post/:id" [id] (show-post id))
  (POST "/post/submit" [title body] (create-post title body))
  (route/files "/" {:root "public"})
  (GET "/" [] (display-posts))
  (route/not-found "Page is not here, no matter how hard we search") 
)
