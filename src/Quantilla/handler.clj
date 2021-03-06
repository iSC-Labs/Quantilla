(ns quantilla.handler
  (:require [quantilla.routes.home :refer [home-routes]]
            [quantilla.routes.auth :refer [auth-routes]]
            [quantilla.middleware :refer [load-middleware]]
            [quantilla.session-manager :as session-manager]
            [quantilla.db.core :refer [redis-con]]
            [noir.response :refer [redirect]]
            [noir.util.middleware :refer [app-handler]]
            [ring.middleware.defaults :refer [site-defaults]]
            [compojure.core :refer [defroutes]]
            [compojure.route :as route]
            [taoensso.timbre :as timbre]
            [taoensso.timbre.appenders.rotor :as rotor]
            [taoensso.carmine.ring :refer [carmine-store]]
            [net.cgrand.enlive-html :as html]
            [environ.core :refer [env]]
            [cronj.core :as cronj]
            ))
(defroutes
  base-routes
  (route/resources "/")
  (route/not-found "Not Found"))

(defn init
  "init will be called once when\r
   app is deployed as a servlet on\r
   an app server such as Tomcat\r
   put any initialization code here"
  []
  (timbre/set-config!
    [:appenders :rotor]
    {:min-level :info,
     :enabled? true,
     :async? false,
     :max-message-per-msecs nil,
     :fn rotor/appender-fn})
  (timbre/set-config!
    [:shared-appender-config :rotor]
    {:path "quantilla.log", :max-size (* 512 1024), :backlog 10})
  (cronj/start! session-manager/cleanup-job)
  (timbre/info
    "
-=[ Quantilla started successfully"
    (when (env :dev) "using the development profile")
    "]=-"))

(defn destroy
  "destroy will be called when your application\r
   shuts down, put any clean up code here"
  []
  (timbre/info "Quantilla is shutting down...")
  (cronj/shutdown! session-manager/cleanup-job)
  (timbre/info "shutdown complete!"))

(def session-defaults
 {:timeout (* 60 30), :timeout-response (redirect "/")})

(defn- mk-defaults
  "set to true to enable XSS protection"
  [xss-protection?]
  (-> site-defaults
   (update-in [:session] merge session-defaults)
   (assoc-in [:security :anti-forgery] xss-protection?)))

(def app
 (app-handler
   [auth-routes home-routes base-routes]
   :session-options {:cookie-name "id"
                     :cookie-attrs {:secure true :http-only true}
                     :timeout (* 10 60)
                     :timeout-response (redirect "/")
                     :store (carmine-store redis-conn
                      {:expiration-secs (* 60 60 15)
                       :key-prefix ""})}
   :middleware
   (load-middleware)
   :ring-defaults
   (mk-defaults true)
   :access-rules
   []
   :formats
   [:json-kw :edn :transit-json]))

