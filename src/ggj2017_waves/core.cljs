(ns ggj2017-waves.core
  (:require [play-cljs.core :as p]
            [goog.events :as events]))

(defonce game (p/create-game 600 400))
(defonce state (atom {:player-x 100
                      :player-y 40
                      :player-image (p/load-image game "player-1.png")
                      :player-wave (p/load-image game "player-2.png")
                      :player-is-waving? false
                      :bg (p/load-image game "city.png")
                      :fruit-store (p/load-image game "fruit.png")
                      :croissant-store (p/load-image game "croissant.png")
                      :coffee-store (p/load-image game "coffee.png")
                      }))

(def speed 10)

(defn move
  "The player can only move if they're not waving.
  Walking _and_ waving is just not cool"
  [direction]
  (if (false? (:player-is-waving? @state))
    (case direction
      :left (swap! state assoc :player-x (- (:player-x @state) speed))
      :right (swap! state assoc :player-x (+ (:player-x @state) speed))
      :up (swap! state assoc :player-y (- (:player-y @state) speed))
      :down (swap! state assoc :player-y (+ (:player-y @state) speed))
      false)))

(defn wave []
  (swap! state assoc :player-is-waving? (not (true? (:player-is-waving? @state)))))

(def main-screen
  (reify p/Screen
    (on-show [this])

    (on-hide [this])

    (on-render [this]
      (let [player (if (:player-is-waving? @state) (:player-wave @state) (:player-image @state))]
        (p/render game
                  [[:div {:x 0 :y 0}
                    [:image {:value (:bg @state)}]
                    [:image {:value (:fruit-store @state) :x 50 :y 50}]
                    [:image {:value (:croissant-store @state) :x 210 :y 280}]
                    [:image {:value (:coffee-store @state) :x 430 :y 130}]]
                   [:image {:value player :x (:player-x @state) :y (:player-y @state)}]
                   ])))))

(doto game
  (p/stop)
  (p/start)
  (p/set-screen main-screen))

(events/listen js/window "keydown"
               (fn [^js/KeyboardEvent event]
                 (let [key (.-keyCode event)]
                   (case key
                     87 (move :up)     ; w
                     65 (move :left)   ; a
                     83 (move :down)   ; s
                     68 (move :right)  ; d
                     false))))

;; using keyup as it is not firing constantly
(events/listen js/window "keyup"
               (fn [^js/KeyboardEvent event]
                 (let [key (.-keyCode event)]
                   (case key
                     32 (wave)         ; space
                     false)
                   (js/console.log key))))
