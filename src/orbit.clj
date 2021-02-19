(ns orbit
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def re
  "radius of the earth"
  6.378e6)
(def me
  "mass of the earth"
  5.972e24)
(def G 6.67430e-11)
(def gm (* me G))
(def t0 0)
(def dt 0.1)
(def dt2 (/ dt 2))

(def t-max (* 20 3600))

(def r0 (+ (* 1 1e3) re))
(def x0 0)
(def y0 r0)
(def vx0 (Math/sqrt (/ gm r0)))
(def vy0 0)


(comment
  (oz/start-server!)

  (plot-path (take-nth 100 (create-points)))

  (plot-all)

  (take 10 (create-points))

  ,)

(defn a [x y]
  (let [r (Math/sqrt (+ (Math/pow x 2) (Math/pow y 2)))]
    [(* -1 x gm (/ 1 (Math/pow r 3)))
     (* -1 y gm (/ 1 (Math/pow r 3)))]))

(defn rk-step [t0 x0 y0 vx0 vy0]
  (let [h dt
        [k1vx k1vy] (a x0 y0)
        k1x vx0
        k1y vy0

        [k2vx k2vy] (a (+ x0 (* k1x (/ h 2)))
                       (+ y0 (* k1y (/ h 2))))
        k2x (* vx0 k1vx (/ h 2))
        k2y (* vy0 k1vy (/ h 2))

        [k3vx k3vy] (a (+ x0 (* k2x (/ h 2)))
                       (+ y0 (* k2y (/ h 2))))
        k3x (* vx0 k2vx (/ h 2))
        k3y (* vy0 k2vy (/ h 2))

        [k4vx k4vy] (a (+ x0 (* k3x h))
                       (+ y0 (* k3y h)))
        k4x (* vx0 k3vx h)
        k4y (* vy0 k3vy h)]
    [(+ x0 (* (+ k1x (* 2 k2x) (* 2 k3x) k4x) (/ h 6)))
     (+ y0 (* (+ k1y (* 2 k2y) (* 2 k3y) k4y) (/ h 6)))
     (+ vx0 (* (+ k1vx (* 2 k2vx) (* 2 k3vx) k4vx) (/ h 6)))
     (+ vy0 (* (+ k1vy (* 2 k2vy) (* 2 k3vy) k4vy) (/ h 6)))]))

(defn create-points
  []
  (loop [x0 x0
         y0 y0
         vx0 vx0
         vy0 vy0
         t 0
         points []]
    (if (>= t t-max)
      points
      (let [[x y vx vy] (rk-step t x0 y0 vx0 vy0)]
        (recur x
               y
               vx
               vy
               (+ t dt)
               (conj points {:t t :x x :y y :vx vx :vy vy}))))))

(defn animate-trajectory [path-data]
  (doall
   (for [datum path-data]
     (do
       (oz/view! {:data {:values [datum]}
                  :encoding {:x {:field "x" :type "quantitative"
                                 :scale {:domain [-1 1]}}
                             :y {:field "y" :type "quantitative"
                                 :scale {:domain [-1 1]}}}
                  :mark "point"})
       (Thread/sleep 100)))))

(defn plot-path [path-data]
  (oz/view! {:data {:values (take-while #(< (:t %) t-max)
                                        path-data)}
             :encoding {:x {:field "x" :type "quantitative"}
                        :y {:field "y" :type "quantitative"}
                        :order {:field "t"}}
             :mark "line"}))


(defn plot-all []
  (oz/view!
   {:data {:values (take-while #(< (:t %) t-max)
                               (take-nth 100 (create-points)))}
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

(defn save-data [path-data]
  (doseq [{:keys [t x y vx vy]} path-data]
    (spit "oscillator.dat" (str/join " " [t x y vx vy "\n"]) :append true)))

(defn -main []
  (do (oz/start-server!)
      (plot-path (create-points))))
