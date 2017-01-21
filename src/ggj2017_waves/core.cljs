(ns ggj2017-waves.core
  (:require [play-cljs.core :as p]
            [ggj2017-waves.utils :as u]
            [goog.events :as events]))

(defonce game (p/create-game 600 400))
(defonce state (atom {:player-x 200
                      :player-y 40
                      :player-image (p/load-image game "player-1.png")
                      :player-wave (p/load-image game "player-2.png")
                      :player-is-waving? false
                      :bg (p/load-image game "city.png")
                      :fruit-store (p/load-image game "fruit.png")
                      :croissant-store (p/load-image game "croissant.png")
                      :coffee-store (p/load-image game "coffee.png")
                      :person-1 (p/load-image game "person-1.png")
                      :person-2 (p/load-image game "person-2.png")
                      :person-3 (p/load-image game "person-3.png")
                      :person-4 (p/load-image game "person-4.png")
                      :house1 (p/load-image game "house1.png")
                      :house2 (p/load-image game "house2.png")
                      :house3 (p/load-image game "house3.png")
                      :entities []
                      }))

(def speed 10)

(defn hypothetical-move-possible?
  "Sorry but my brain can't think of a more elegant way right now.
  Basically I'm checking whether or not a move in the wanted direction
  would trigger a collision by duplicating a lot of code"
  [direction]
  (let [fake-player
        {:value :player-wave
         :x (:player-x @state)
         :y (:player-y @state)
         :width 20
         :height 30}
        fake (case direction
          :left (assoc fake-player :x (- (:player-x @state) speed))
          :right (assoc fake-player :x (+ (:player-x @state) speed))
          :up (assoc fake-player :y (- (:player-y @state) speed))
          :down (assoc fake-player :y (+ (:player-y @state) speed))
          false)]
    (not (u/collision-detection (:entities @state) [:image  fake]))))

(defn move
  "The player can only move if they're not waving.
  Walking _and_ waving is just not cool.
  Also check for things you can't move through."
  [direction]
  (if (false? (:player-is-waving? @state))
    (if (hypothetical-move-possible? direction)
      (case direction
        :left (swap! state assoc :player-x (- (:player-x @state) speed))
        :right (swap! state assoc :player-x (+ (:player-x @state) speed))
        :up (swap! state assoc :player-y (- (:player-y @state) speed))
        :down (swap! state assoc :player-y (+ (:player-y @state) speed))
        false))))

(defn wave []
  (swap! state assoc :player-is-waving? (not (true? (:player-is-waving? @state)))))

(defn init-entities []
  [[:image {:value (:fruit-store @state) :x 50 :y 50 :width 50 :height 50}]
   [:image {:value (:croissant-store @state) :x 210 :y 280 :width 50 :height 50}]
   [:image {:value (:coffee-store @state) :x 430 :y 130 :width 50 :height 50}]])

(def main-screen
  (reify p/Screen
    (on-show [this]
      (swap! state assoc :entities (init-entities)))
    (on-hide [this])
    (on-render [this]
      (let [player-state (if (:player-is-waving? @state) (:player-wave @state) (:player-image @state))
            player-img [:image {:value player-state :x (:player-x @state) :y (:player-y @state) :width 20 :height 30}]
            entities (:entities @state)]

        (when (u/collision-detection entities player-img) (js/console.log "HIT"))

        (p/render game [[:div {:x 0 :y 0}
                         [:image {:value (:bg @state)}]]])
        (p/render game entities)
        (p/render game [player-img])))))

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
