(ns challenge.ui.core
  "Main namespace for application orchestration and initialization."
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [challenge.ui.models :as models]))

(defonce app-state (r/atom models/initial-state))

(defn app
  "Main application component."
  []
  (let [_state @app-state]
    [:div.max-w-6xl.mx-auto.bg-white.rounded-xl.shadow-2xl.overflow-hidden
     [:header.bg-gradient-to-r.from-indigo-500.to-purple-600.text-white.p-8.text-center
      [:h1.text-4xl.md:text-5xl.mb-2.5 "Challenge"]
      [:p.text-lg.opacity-90 "Notifications & Delivery"]]
     [:main.p-8.md:p-8
      [:section.mb-10
       [:h2.text-2xl.mb-5.text-gray-800 "Notifications"]
       [:p.text-gray-600 "Submit and manage notifications."]]
      [:section.mb-10
       [:h2.text-2xl.mb-5.text-gray-800 "Delivery"]
       [:p.text-gray-600 "View delivery history."]]]]))

(defn mount-root
  "Mounts root application component."
  []
  (when-let [app-el (.getElementById js/document "app")]
    (rdom/render [app] app-el)))

(defn ^:export init
  "Exported initialization function to be called by HTML or on namespace load."
  []
  (mount-root))

(init)
