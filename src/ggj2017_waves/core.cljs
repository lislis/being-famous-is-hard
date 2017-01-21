(ns ggj2017-waves.core
  (:require [play-cljs.core :as p]
            [goog.events :as events]))

(defonce game (p/create-game 600 400))
(defonce state (atom {:player-x 100
                      :player-y 40
                      :player-image nil}))

(def speed 10)

(defn move [direction]
  (case direction
    :left (swap! state assoc :player-x (- (:player-x @state) speed))
    :right (swap! state assoc :player-x (+ (:player-x @state) speed))
    :up (swap! state assoc :player-y (- (:player-y @state) speed))
    :down (swap! state assoc :player-y (+ (:player-y @state) speed))
    false))

(def main-screen
  (reify p/Screen
    (on-show [this]
      (swap! state assoc
             :player-image (p/load-image game "player-1.png")
             :bg (p/load-image game "city.png")))

    (on-hide [this])

    (on-render [this]
      (p/render game
                [[:div {:x 0 :y 0}
                  [:image {:value (:bg @state)}]]
                 [:image {:value (:player-image @state) :x (:player-x @state) :y (:player-y @state)}]
                 ]))))

(doto game
  (p/stop)
  (p/start)
  (p/set-screen main-screen))

;;(events/listen js/window "mousemove"
;;               (fn [^js/KeyboardEvent event]
;;                 (js/console.log event)))

(events/listen js/window "keydown"
               (fn [^js/KeyboardEvent event]
                 (let [key (.-keyCode event)]
                   (case key
                     87 (move :up)     ; w
                     65 (move :left)   ; a
                     83 (move :down)   ; s
                     68 (move :right)  ; d
                     false)
                   (js/console.log key))))
