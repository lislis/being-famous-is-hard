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

(defn init-entities []
  [[:image {:value (:fruit-store @state) :x 50 :y 50 :width 50 :height 50}]
   [:image {:value (:croissant-store @state) :x 210 :y 280 :width 50 :height 50}]
   [:image {:value (:coffee-store @state) :x 430 :y 130 :width 50 :height 50}]])

;; copied from
;; https://github.com/oakes/play-cljs-examples/blob/master/flappy-bird-clone/src/flappy_bird_clone/core.cljs#L41
(defn collision-detection [images [_ {:keys [x y width height] :as bird}]]
  (let [diags (map
               (fn [[_ {:keys [x y width height] :as image}]]
                 {:x1 x :y1 y :x2 (+ x width) :y2 (+ y height)})
               images)
        overlap-check (fn [{:keys [x1 y1 x2 y2]}]
                        (let [birdx1 x birdy1 y birdx2 (+ x 60) birdy2 (+ y 60)]
                          (cond
                            (< birdx2 x1) false
                            (> birdx1 x2) false
                            (> birdy1 y2) false
                            (> y1 birdy2) false
                            :overlapping true)))
        overlaps (map overlap-check diags)]
    (some #(= true %) overlaps)))

(def main-screen
  (reify p/Screen
    (on-show [this]
      (swap! state assoc :entities (init-entities)))
    (on-hide [this])
    (on-render [this]
      (let [player-state (if (:player-is-waving? @state) (:player-wave @state) (:player-image @state))
            player-img [:image {:value player-state :x (:player-x @state) :y (:player-y @state) :width 20 :height 30}]
            entities (:entities @state)]

        (when (collision-detection entities player-img) (js/console.log "HIT"))

        (p/render game
                  [[:div {:x 0 :y 0}
                    [:image {:value (:bg @state)}]]
                   player-img])

        (p/render game entities)))))

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
