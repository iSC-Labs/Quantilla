(ns quantilla.routes.dashboard
  "Handles Routes and processing for the Dashboard page"
  (:require [compojure.core :refere :all]
            [noir.response :refer [redirect]]
            [quantilla.layout :as layout]
            [quantilla.db.core :as db]))

(defroutes home-routes
  (GET "/dashboard" (layout/render-dashboard))
  )