(ns conical-pendulum
  (:require [clojure.string :as str]
            [clojure.java.shell :refer [sh]]
            [gnuplot.core :as g]))

(def g 9.81)

(defn create-points
  [{:keys [l
           theta0
           t0 tf dt]}]
  (let [omega (Math/sqrt (/ g l))
        period (/ (* 2 Math/PI) omega)]
    (for [t (range t0 tf dt)]
      (let [theta (* theta0 omega (Math/cos (* omega (- t t0))))
            dtheta-dt (* -1 omega theta0 (Math/sin (* omega (- t t0))))
            x (* l (Math/sin theta))
            y (* -1 l (Math/cos theta))
            vx (* l dtheta-dt (Math/cos theta))
            vy (* l dtheta-dt (Math/sin theta))]
        (str/join ", " [t x y vx vy theta dtheta-dt])))))

(def initial-values {:l 1
                     :theta0 0.314
                     :t0 0
                     :tf 20
                     :dt 0.01})

(def gnuplot-script "
set title 'Conical Pendulum';
plot 'conical_pendulum.dat' using 2:3 w l t 'y(x)';
")

(defn create-script [path-data]
  (format "
set title 'Conical Pendulum';
set xrange [0:%s];
do for [ii=1:%s] {
  plot 'conical_pendulum.dat' every ::1::ii w l ls 1, 'conical_pendulum.dat' every ::ii::ii w p ls 1
}
" (:tf initial-values) (- (count path-data) 1)))

(defn plot-path [path-data]
  (spit "conical_pendulum.dat" (str/join "\n" path-data))
  (println (create-script path-data))
  (sh "gnuplot" "-p" "-e" (create-script path-data)))

(defn -main []
  (do (plot-path (create-points initial-values))))

