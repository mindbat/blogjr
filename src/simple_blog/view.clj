(ns simple-blog.view
  (:use hiccup.core, hiccup.form-helpers, hiccup.page-helpers)
)

; Function to create html for a sidebar
(defn display-sidebar []
  (html
    [:section {:class "sidebar"}
      [:p [:a {:href "/post/new"} "New post"]]
    ]
  )
)

; Setup general page layout and include any js and css files we need
(defn layout [title body]
  (html5
    [:head 
      [:title title]
      (include-css "/css/blog.css")
    ]
    [:body
      [:header
        [:h1 
          [:a {:href "/"} "Simple Blog"]
        ]
      ]
      (display-sidebar)
      body
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
