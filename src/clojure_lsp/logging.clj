(ns clojure-lsp.logging
  (:require
    [clojure.string :as str]
    [taoensso.timbre :as log]
    [taoensso.encore :as enc]))

(set! *warn-on-reflection* true)

(defn output-fn
  ([data]
   (let [{:keys [level ?err #_vargs msg_ ?ns-str ?file
                 timestamp_ ?line]} data]
     (str
       (when-let [ts (force timestamp_)] (str ts " "))
       (str/upper-case (name level))  " "
       "[" (or ?ns-str ?file "?") ":" (or ?line "?") "] - "
       (force msg_)
       (when-let [err ?err]
         (str enc/system-newline (log/stacktrace err {})))))))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn setup-logging [db]
  (let [log-path (str (java.io.File/createTempFile "clojure-lsp." ".out"))]
    (log/merge-config! {:output-fn output-fn
                        :appenders {:println {:enabled? false}
                                    :spit (log/spit-appender {:fname log-path})}})
    (log/handle-uncaught-jvm-exceptions!)
    (swap! db assoc :log-path log-path)))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn update-log-path [log-path db]
  (log/merge-config! {:appenders {:spit (log/spit-appender {:fname log-path})}})
  (swap! db assoc :log-path log-path))

#_{:clj-kondo/ignore [:unresolved-var]}
(defn set-log-to-stdout []
  (log/merge-config! {:appenders {:println (log/println-appender {:stream :auto})}}))
