(set-env!
  :source-paths #{"src"}
  :dependencies '[
                  [adzerk/boot-cljs "1.7.170-3" :scope "test"]
                  [adzerk/boot-test "1.0.6" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  [org.clojure/clojurescript "1.7.170"]])

(require '[adzerk.boot-cljs :refer :all]
         '[adzerk.boot-test :refer :all]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]])


(task-options!
  pom  {:project     'org.prognostic/cursor
        :version     "0.0.1-SNAPSHOT"})

(deftask testing []
         (merge-env! :source-paths #{"test"})
         identity)

(deftask clj-test []
         (comp (testing)
               (test)))

(deftask cljs-test []
         (comp (testing)
               (test-cljs :js-env :node
                          :exit?  true)))

(deftask auto-cljs-test []
         (comp (testing)
               (watch)
               (test-cljs :js-env :node)))

(deftask test-all []
         (comp (clj-test)
               (cljs-test)))
