(ns ggj2017-waves.config
  (:require [play-cljs.core :as p]))

(defn init-stores [state]
  [[:image {:value (:fruit-store state) :x 50 :y 50 :width 50 :height 50}]
   [:image {:value (:croissant-store state) :x 210 :y 280 :width 50 :height 50}]
   [:image {:value (:coffee-store state) :x 430 :y 130 :width 50 :height 50}]])

(defn init-entities [game]
  [[:image {:value (p/load-image game (str "house" (rand-int 3) ".png"))
            :x 300 :y 20 :width 50 :height 50}]
   [:image {:value (p/load-image game (str "house" (rand-int 3) ".png"))
            :x 80 :y 300 :width 50 :height 50}]
   [:image {:value (p/load-image game (str "house" (rand-int 3) ".png"))
            :x 230 :y 170 :width 50 :height 50}]
   [:image {:value (p/load-image game (str "house" (rand-int 3) ".png"))
            :x 430 :y 210 :width 50 :height 50}]])
