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
