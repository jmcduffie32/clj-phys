(ns orbit
  (:require [clojure.string :as str]
            [oz.core :as oz]))

(def re
  "radius of the earth"
  6.3781e6)
(def me
  "mass of the earth"
  5.972e24)
(def G 6.67430e-11)
(def gm (* me G))
(def drag )
(def t0 0)
(def tf 20)
(def dt 0.01)
(def dt2 (/ dt 2))

(defn a [x y vx vy t]
  (let [r (Math/sqrt (+ (* x x) (* y y)))
        v2 (+ (* vx vx) (* vy vy))
        v1 (Math/sqrt v2)]
    [(* -1 x gm (/ 1 (* r r r)))
     (* -1 y gm (/ 1 (* r r r)))]))

(defn rk-step [t0 x0 y0 vx0 yx0]
  (let [t1 (+ t0 dt)
        th (+ t0 (/ dt 2))
        [ax0 ay0] (a x0 y0 vx0 vy0 t0)
        kx1 (* dt2 ax0)
        ky1 (* dt2 ay0)
        lx1 (* dt2 vx0)
        ly1 (* dt2 vy0)
        [ax1 ay1] (a (+ x0 lx1) (+ y0 ly1) (+ vx0 kx1) (+ vy0 ky1))
        kx2 (* dt2 ax1)
        ky2 (* dt2 ay1)
        lx2 (* dt2 (+ vx0 kx1))
        ly2 (* dt2 (+ vy0 ky1))
        [ax2 ay2] (a (+ x0 lx2) (+ y0 ly2) (+ vx0 kx2) (+ vy0 ky2))
        kx3 (* dt2 ax2)
        ky3 (* dt2 ay2)
        lx3 (* dt2 (+ vx0 kx2))
        ly3 (* dt2 (+ vy0 ky2))
        [ax3 ay3] (a (+ x0 lx3) (+ y0 ly3) (+ vx0 kx3) (+ vy0 ky3))
        kx4 (* dt2 ax3)
        ky4 (* dt2 ay3)
        lx4 (* dt2 (+ vx0 kx3))
        ly4 (* dt2 (+ vy0 ky3))]
    [(+ x0 (/ (+ lx1 (* 2 lx2) lx3 lx4) 3))
     (+ y0 (/ (+ ly1 (* 2 ly2) ly3 ly4) 3))
     (+ vx0 (/ (+ kx1 (* 2 kx2) kx3 kx4) 3))
     (+ vy0 (/ (+ ky1 (* 2 ky2) ky3 ky4) 3))]))

(defn runge-kutta )
(defn create-points
  []
  (for [t (range t0 tf dt)]
    {:t t :x (x t) :y (y t) :vx (vx t) :vy (vy t)}))

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
  (oz/view! {:data {:values (take-while #(< (:t %) tf)
                                        path-data)}
             :encoding {:x {:field "x" :type "quantitative"}
                        :y {:field "y" :type "quantitative"}
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

(defn save-data [path-data]
  (doseq [{:keys [t x y vx vy]} path-data]
    (spit "oscillator.dat" (str/join " " [t x y vx vy "\n"]) :append true)))

(defn -main []
  (do (oz/start-server!)
      (plot-path (create-points))))
