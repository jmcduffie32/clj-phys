(ns conical-pendulum
  (:require [clojure.string :as str]
            [selmer.parser :as s]))

(def g 9.81)

(defn create-points
  [{:keys [l omega
           t0 tf dt]}]
  (let [period (/ (* 2 Math/PI) omega)
        cos_theta (/ g (* omega omega l))
        sin_theta (Math/sqrt (- 1 (* cos_theta cos_theta)))
        z (/ (* -1 g) (* omega omega))
        vz 0
        r (/ (* sin_theta g) (* omega omega cos_theta))]
    (for [t (range t0 tf dt)]
      (let [x (* r (Math/cos (* omega t)))
            y (* r (Math/sin (* omega t)))
            vx (* -1 r (Math/sin (* omega t)) omega)
            vy (* r (Math/cos (* omega t)) omega)]
        [t x y z vx vy vz]))))

(def initial-values {:l 1
                     :omega 6.28
                     :t0 0
                     :tf 10
                     :dt 0.01})

(defn create-script [{:keys [path animate]}]
  (if animate
    (s/render "
set title 'Conical Pendulum';
set xrange [{{x-min}}:{{x-max}}];
set yrange [{{y-min}}:{{y-max}}];
set zrange [{{z-min}}:{{z-max}}];
do for [ii=1:{{iterations}}] {
  splot 'conical_pendulum.dat' every ::1::ii using 2:3:4 w l ls 1 notitle,
  'conical_pendulum.dat' every ::ii::ii using 2:3:4 w p pt 7 ps 4 notitle;
}
" {:x-min (nth (apply min-key #(nth % 1) path) 1)
   :x-max (nth (apply max-key #(nth % 1) path) 1)
   :y-min (nth (apply min-key #(nth % 2) path) 2)
   :y-max (nth (apply max-key #(nth % 2) path) 2)
   :z-min -0.5 ;;(nth (apply min-key #(nth % 3) path) 3)
   :z-max 0 ;;(nth (apply max-key #(nth % 3) path) 3)
   :iterations (- (count path) 1)} )
    (str "
set title 'Conical Pendulum';
splot 'conical_pendulum.dat' using 2:3:4 w l t 'y(x)';
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

