(ns quantilla.layout
  (:require [net.cgrand.enlive-html :as html]
            [ring.util.response :refer [content-type response]]
            [compojure.response :refer [Renderable]]
            [noir.session :as session]))

;; Rendering Logic for Enlive Templates.
;;===========================================

;; Home page templating.
(html/deftemplate home "../resources/templates/homeBase.html" [body]
  [:#mainBody]
  (html/append body))

(html/defsnippet homeLogin "../resources/templates/homeSnippets.html" [:div#login]
  [post]
  [:div#login] (html/append (:message post)))

(html/defsnippet homeLostPass "../resources/template/homeSnippets.html" [:div#getPass]
  [post]
  [:div#getPass] (html/append (:email addr)))

(html/defsnippet homeLostId "../resources/templates/homeSnippets.html" [:div#getId]
  [post]
  [:div#getId] (html/append (:email addr)))

(html/defsnippet homeRegister "../resources/templates/homeSnippets.html" [:div#register]
  [post]
  [:div#register] (html/append (:id post :id-error post :pass1-error post :pass2-error post :secret-error post)))

(html/defsnippet homeProfile "../resources/templates/homeSnippets.html" [:div#profile]
  [post]
  [:p#profileUser] (html/content (:id post))
  [:p#profileName] (html/content (:name post))
  [:p#profileEmail] (html/content (:email post)))

(defn render-login [errors]
  (home [(homeLogin) errors]))

(defn render-lost-id [errors]
  (home [(homeLostId errors)]))

(defn render-lost-Pass [errors]
  (home [(homeLostPass errors)]))

(defn render-register [errors]
  (home [(homeRegister errors)]))

(defn render-profile [details]
  (home [(homeProfile details)]))

;;=========================================================================;;
;; Dashboard Templating.
(html/deftemplate dashboard "../resources/templates/dashboard.html"
  [body errors]

  [:#mainBody]
  (html/append body errors))

;; Portfolio Templating.
;; Risk Templating.
;;

