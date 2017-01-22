(ns ggj2017-waves.core
  (:require [play-cljs.core :as p]
            [ggj2017-waves.utils :as u]
            [ggj2017-waves.config :as c]
            [goog.events :as events]))

(defonce game (p/create-game 600 400))
(defonce state (atom {:player-x 200
                      :player-y 40
                      :player-image (p/load-image game "player-1.png")
                      :player-wave (p/load-image game "player-2.png")
                      :player-is-waving? false
                      :bg (p/load-image game "city.png")
                      :title (p/load-image game "start-screen.png")
                      :intro-1 (p/load-image game "story-1.png")
                      :intro-2 (p/load-image game "story-2.png")
                      :game-over-1 (p/load-image game "game-over-timeout.png")
                      :game-over-2 (p/load-image game "game-over-fameout.png")
                      :game-over-3 (p/load-image game "game-over-win.png")
                      :entities []
                      :people []
                      :stores []
                      :spawn-timer 0
                      :bag []
                      :fame 100
                      :time 0
                      }))

(declare game-over-1-screen)

(def speed 5)
(def people-spawn-interval 4000)
(def fame-factor 0.2)
(def break-time 180)

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

(defn spawn-person []
  (let [new-entity [:image {:value (p/load-image game (str "person-" (rand-int 4) ".png"))
                            :x (rand-int 580) :y (rand-int 320) :width 20 :height 30}]
        old-entities (:people @state)
        new-entities (conj old-entities new-entity)]
    (swap! state assoc :people new-entities)))

(defn spawn-person-maybe []
  (let [delta (p/get-delta-time game)
        timer (:spawn-timer @state)
        update-time (+ timer delta)]
    (if (> update-time people-spawn-interval)
      (do
        (spawn-person)
        (swap! state assoc :spawn-timer 0))
      (swap! state assoc :spawn-timer update-time))))

(defn update-fame []
  (let [fame (:fame @state)
        new-fame (- fame fame-factor)]
    (swap! state assoc :fame new-fame)))

(def main-screen
  (reify p/Screen
    (on-show [this]
      (swap! state assoc :time 0)
      (swap! state assoc :stores (c/init-stores game))
      (swap! state assoc :entities (c/init-entities game)))
    (on-hide [this])
    (on-render [this]
      (let [player-state (if (:player-is-waving? @state)
                           (:player-wave @state)
                           (:player-image @state))
            player-img [:image {:value player-state
                                :x (:player-x @state) :y (:player-y @state)
                                :width 20 :height 30}]
            entities (:entities @state)
            people (:people @state)
            stores (:stores @state)]

        (let [time (:time @state)
              delta (p/get-delta-time game)
              more-time (+ time delta)]
          (swap! state assoc :time more-time))

        (spawn-person-maybe)

        (when (u/collision-detection people player-img) (update-fame))
        (when (u/collision-detection stores player-img) (js/console.log "Shop"))

        (when (= (int (/ (:time @state) 1000)) break-time)
          (p/set-screen game game-over-1-screen))

        ;;()

        (p/render game [[:image {:value (:bg @state) :x 0 :y 0}]])
        (p/render game entities)
        (p/render game stores)
        (p/render game people)
        (p/render game [player-img
                        [:fill {:color "lightgrey"}
                         [:stroke {:color "lightgrey"}
                          [:rect {:x 0 :y 350 :width 600 :height 50}]]]
                        [:text {:value (str "FAME: " (int (:fame @state)))
                                :x 20 :y 385 :size 20
                                :font "Georgia" :style :bold}]
                        [:text {:value (str "TIME AWAY: " (int (/ (:time @state) 1000)))
                                :x 170 :y 385 :size 20
                                :font "Georgia" :style :bold}]
                        [:text {:value (str "BAG: " (:bag @state))
                                :x 380 :y 385 :size 20
                                :font "Georgia" :style :bold}]])))))

(def title-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (p/render game [[:image {:value (:title @state) :x 0 :y 0}]]))))

(def intro-1-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (p/render game [[:image {:value (:intro-1 @state) :x 0 :y 0}]]))))

(def intro-2-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (p/render game [[:image {:value (:intro-2 @state) :x 0 :y 0}]]))))

(def game-over-1-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (p/render game [[:image {:value (:game-over-1 @state) :x 0 :y 0}]]))))

(def game-over-2-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (p/render game [[:image {:value (:game-over-2 @state) :x 0 :y 0}]]))))

(def game-over-3-screen
  (reify p/Screen
    (on-show [this])
    (on-hide [this])
    (on-render [this]
      (p/render game [[:image {:value (:game-over-3 @state) :x 0 :y 0}]]))))

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
                 (let [screen (p/get-screen game)
                       key (.-keyCode event)]
                   (case key
                     32 (cond
                          (= screen title-screen) (p/set-screen game intro-1-screen)
                          (= screen intro-1-screen) (p/set-screen game intro-2-screen)
                          (= screen intro-2-screen) (p/set-screen game main-screen)
                          (= screen game-over-1-screen) (p/set-screen game main-screen)
                          (= screen game-over-2-screen) (p/set-screen game main-screen)
                          (= screen game-over-3-screen) (p/set-screen game main-screen)
                          (= screen main-screen) (wave))         ; space
                     false))))
