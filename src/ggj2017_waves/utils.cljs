(ns ggj2017-waves.utils
  (:require [play-cljs.core :as p]))

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

(defn hypothetical-move-possible?
  "Sorry but my brain can't think of a more elegant way right now.
  Basically I'm checking whether or not a move in the wanted direction
  would trigger a collision by duplicating a lot of code"
  [direction speed state]
  (let [fake-player
        {:value :player-wave
         :x (:player-x state)
         :y (:player-y state)
         :width 20
         :height 30}
        fake (case direction
               :left (assoc fake-player :x (- (:player-x state) speed))
               :right (assoc fake-player :x (+ (:player-x state) speed))
               :up (assoc fake-player :y (- (:player-y state) speed))
               :down (assoc fake-player :y (+ (:player-y state) speed))
               false)]
    (not (collision-detection (:entities state) [:image fake]))))
