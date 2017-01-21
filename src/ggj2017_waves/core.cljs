(ns ggj2017-waves.core
  (:require [play-cljs.core :as p]))

(defonce game (p/create-game 600 400))
(defonce state (atom {}))

(def main-screen
  (reify p/Screen
    (on-show [this]
      (reset! state {:text-x 20 :text-y 30}))

    (on-hide [this])

    (on-render [this]
      (p/render game
        [[:fill {:color "pink"}
          [:rect {:x 0 :y 0 :width 600 :height 400}]]
         [:fill {:color "black"}
          [:text {:value "Hello, world!" :x (:text-x @state) :y (:text-y @state) :size 16 :font "Georgia" :style :italic}]]])
      (swap! state update :text-x inc))))

(doto game
  (p/stop)
  (p/start)
  (p/set-screen main-screen))
