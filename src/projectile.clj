(ns projectile
  (:require [clojure.string :as str]
            [oz.core :as oz]))

;; initial values and physical constants
(def g 2)
(def k 1)
(def v0 10)
(def theta (/ (* Math/PI 30) 180))
(def t0 0)
(def tf 5)
(def dt 0.01)

;; Definitions of motion
(def v0x
  (* v0 (Math/cos theta)))

(def v0y
  (* v0 (Math/sin theta)))

;; With air resistance

;; (defn x
;;   "x position with resistance"
;;   [t]
;;   (* (/ v0x k) (- 1 (Math/exp (* -1 k t)))))

;; (defn y [t]
;;   "y position with resistance"
;;   (- (* (/ 1 k)
;;         (+ v0y (/ g k))
;;         (- 1 (Math/exp (* -1 k t))))
;;      (* (/ g k)
;;         t)))

;; (defn vx
;;   "vx with resistance"
;;   [t]
;;   (* v0x (Math/exp (* -1 k t))))

;; (defn vy
;;   "vy with resistance"
;;   [t]
;;   (- (* (+ v0y (/ g k)) (Math/exp (* -1 k t))) (/ g k)))

;; Without air resistance

(defn x
  "x position without resistance"
  [t]
  (* v0x t))

(defn y
  "y position without resistance"
  [t]
  (- (* v0y t) (/ (* g t t) 2)))

(defn vx
  "vx without resistance"
  [t]
  v0x)

(defn vy
  "vy without resistance"
  [t]
  (- v0y (* g t)))


(defn create-points
  "Generate data points for given equations of motion"
  []
  (for [t (range t0 tf dt)]
    {:t t :x (x t) :y (y t) :vx (vx t) :vy (vy t)}))


(defn animate-trajectory [path-data]
  (doall
   (for [datum path-data]
     (do
       (oz/view! {:data {:values [datum]}
                  :encoding {:x {:field "x" :type "quantitative"
                                 :scale {:domain [0 20]}}
                             :y {:field "y" :type "quantitative"
                                 :scale {:domain [0 20]}}}
                  :mark "point"})
       (Thread/sleep 100)))))

(defn plot-path [path-data]
  (oz/view! {:data {:values path-data}
             :encoding {:x {:field "x" :type "quantitative"
                            :scale {:domain [0 20]}}
                        :y {:field "y" :type "quantitative"
                            :scale {:domain [0 20]}}
                        :order {:field "t"}}
             :mark "line"}))


(defn plot-all []
  (oz/view!
   {:data {:values (take-while #(< (:t %) tf)
                               (create-points))}
    :columns 2
    :concat
    [
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "x" :type "quantitative"}
                 :order {:field "t"}}}
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "y" :type "quantitative"}
                 :order {:field "t"}}}
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "vx" :type "quantitative"}
                 :order {:field "t"}}}
     {:mark "line"
      :encoding {:x {:field "t" :type "quantitative"}
                 :y {:field "vy" :type "quantitative"}
                 :order {:field "t"}}}
     ]}))

(defn -main []
  (do (oz/start-server!)
      (plot-path (create-points))))

(comment
  (oz/start-server!)

  (plot-all)

  (plot-path (create-points))

  ,)
