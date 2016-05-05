(set-env!
  :source-paths #{"src"}
  :dependencies '[[adzerk/bootlaces "0.1.13" :scope "test"]
                  [adzerk/boot-cljs "1.7.170-3" :scope "test"]
                  [adzerk/boot-test "1.0.6" :scope "test"]
                  [crisptrutski/boot-cljs-test "0.2.2-SNAPSHOT" :scope "test"]
                  [org.clojure/clojurescript "1.7.170"]])

(require '[adzerk.boot-cljs :refer :all]
         '[adzerk.boot-test :as boot-test]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]]
         '[adzerk.bootlaces :refer :all])

(def +version+ "0.0.1-SNAPSHOT")
(bootlaces! +version+)


(task-options!
  pom {:project 'org.prognostic/cursor
       :version +version+}
  test-cljs {:js-env :node})

(deftask testing []
         (merge-env! :source-paths #{"test"})
         identity)

(deftask test []
         (comp (testing)
               (boot-test/test)
               (test-cljs)))
