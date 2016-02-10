(set-env!
  :source-paths #{"src"}
  :dependencies '[
                  [adzerk/boot-cljs "1.7.170-3" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  [org.clojure/clojurescript "1.7.170"]])

(require '[adzerk.boot-cljs :refer :all]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])


(task-options!
  pom  {:project     'org.prognostic-llc/cljs-cursor
        :version     "0.0.1-SNAPSHOT"})

(deftask testing []
         (merge-env! :source-paths #{"test"})
         identity)

;;; This prevents a name collision WARNING between the test task and
;;; clojure.core/test, a function that nobody really uses or cares
;;; about.
(ns-unmap 'boot.user 'test)

(deftask test []
         (comp (testing)
               (test-cljs :js-env :node
                          :exit?  true)))
(deftask auto-test []
         (comp (testing)
               (watch)
               (test-cljs :js-env :node)))