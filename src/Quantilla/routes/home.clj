(ns quantilla.routes.home
  "Login and Lost Id page routes and functions"
  (:require [compojure.core :refer :all]
            [crypto.random :refer [base64]]
            [noir.response :refer [redirect]]
            [quantilla.layout :as layout]
            [quantilla.util :refer [send-email]]
            [quantilla.db.core :as db]))

(defn sendEmail [flag addr]
  (if-let [id (db/get-user addr)];;Find email!
    (if (flag)
      (send-email [addr {:user-id id}])
      (send-email [addr {:password (base64 10)}]))
    (if (flag)
      (layout/render-lost-id
        {:email "No user with specified email found"})
      (layout/render-lost-Pass
        {:email "No user with specified email found"})))
  (redirect "/"))

(defroutes home-routes
  (GET "/" [] (layout/render-login))
  (GET "/forgotId" (layout/render-lost-id))
  (GET "/forgotPass" (layout/render-lost-Pass))
  (POST "/sendId" {params :params}
    (sendEmail [true (params idEmail)]))
  (POST "/sendPass" {params :params}
    (sendEmail [false (params idEmail)])))
  
