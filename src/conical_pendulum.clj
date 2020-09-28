(ns conical-pendulum
  (:require [clojure.string :as str]
            [selmer.parser :as s]))

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
        [t x y vx vy theta dtheta-dt]))))

(def initial-values {:l 1
                     :theta0 0.314
                     :t0 0
                     :tf 20
                     :dt 0.01})

(def gnuplot-script "
set title 'Conical Pendulum';
plot 'conical_pendulum.dat' using 2:3 w l t 'y(x)';
")

(defn create-script [{:keys [path animate]}]
  (if animate
    (s/render "
set title 'Conical Pendulum';
set xrange [{{x-min}}:{{x-max}}];
set yrange [{{y-min}}:{{y-max}}];
do for [ii=1:{{iterations}}] {
  plot 'conical_pendulum.dat' every ::1::ii using 2:3 w l ls 1 notitle,
 'conical_pendulum.dat' every ::ii::ii using 2:3 w p pt 7 ps 4 notitle;
}
" {:x-min (nth (apply min-key #(nth % 1) path) 1)
   :x-max (nth (apply max-key #(nth % 1) path) 1)
   :y-min (nth (apply min-key #(nth % 2) path) 2)
   :y-max (nth (apply max-key #(nth % 2) path) 2)
   :iterations (- (count path) 1)} )
    (str "
set title 'Conical Pendulum';
plot 'conical_pendulum.dat' using 2:3 w l t 'y(x)';
")))


(defn create-data-file [path]
  (spit "conical_pendulum.dat" (str/join "\n" (map #(str/join ", " %) path))))

(defn plot-path [{:keys [path animate] :as opts}]
  (create-data-file path)
  (println (create-script opts))
  (-> (ProcessBuilder. ["gnuplot" "-p" "-e" (create-script opts)])
      (.start)))

(defn -main []
  (do (plot-path {:path (create-points initial-values)
                  :animate true})))

