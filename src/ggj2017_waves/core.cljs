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
                      :house1 (p/load-image game "house1.png")
                      :house2 (p/load-image game "house2.png")
                      :house3 (p/load-image game "house3.png")
                      :entities []
                      :people []
                      :spawn-timer 0
                      }))

(def speed 10)
(def people-spawn-interval 4000)

(defn move [direction]
  (if (false? (:player-is-waving? @state))
    (if (u/hypothetical-move-possible? direction speed @state)
      (case direction
        :left (swap! state assoc :player-x (- (:player-x @state) speed))
        :right (swap! state assoc :player-x (+ (:player-x @state) speed))
        :up (swap! state assoc :player-y (- (:player-y @state) speed))
        :down (swap! state assoc :player-y (+ (:player-y @state) speed))))))

(defn wave []
  (swap! state assoc :player-is-waving? (not (true? (:player-is-waving? @state)))))

(defn init-entities []
  [[:image {:value (:fruit-store @state) :x 50 :y 50 :width 50 :height 50}]
   [:image {:value (:croissant-store @state) :x 210 :y 280 :width 50 :height 50}]
   [:image {:value (:coffee-store @state) :x 430 :y 130 :width 50 :height 50}]])

(defn spawn-person
  "Pick a person image and append it to people"
  []
  (let [new-entity [:image {:value (p/load-image game (str "person-" (rand-int 4) ".png"))
                            :x (rand-int 580) :y (rand-int 370) :width 20 :height 30}]
        old-entities (:people @state)
        new-entities (conj old-entities new-entity)]
    (swap! state assoc :people [new-entities])))

(defn spawn-person?
  "spawns a person approx every 2 seconds"
  []
  (let [delta (p/get-delta-time game)
        timer (:spawn-timer @state)
        update-time (+ timer delta)]
    (if (> update-time people-spawn-interval)
      (do
        (spawn-person)
        (swap! state assoc :spawn-timer 0))
      (swap! state assoc :spawn-timer update-time))))

(def main-screen
  (reify p/Screen
    (on-show [this]
      (swap! state assoc :entities (init-entities)))
    (on-hide [this])
    (on-render [this]
      (let [player-state (if (:player-is-waving? @state) (:player-wave @state) (:player-image @state))
            player-img [:image {:value player-state
                                :x (:player-x @state) :y (:player-y @state)
                                :width 20 :height 30}]
            entities (:entities @state)
            people (:people @state)]

        (spawn-person?)

        (when (u/collision-detection people player-img) (js/console.log "BUM"))
        (when (u/collision-detection entities player-img) (js/console.log "HIT"))

        (p/render game [[:image {:value (:bg @state) :x 0 :y 0}]])
        (p/render game entities)
        (p/render game people)
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
                   ;(js/console.log key)
                   )))
