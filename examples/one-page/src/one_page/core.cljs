(ns one-page.core
  (:require [full-control.core :as fc :refer-macros [defpage]]))

(enable-console-print!)

(def app-state (atom {:menu-h "MyMenuH"
                      :panel-title "MyPanel"
                      :panel-text "Hello panel!"
                      :menu-v "MyMenuV"}))

(defpage page [cursor owner opts]
  (render-state [st]
                (navbar (brand {:href "#"
                                :on-click (fn [_] (js/alert "This is my brand!"))}
                               (:menu-h cursor))
                        (link {:href "#"
                               :on-click (fn [_] (js/alert "You're home!"))} "Home")
                        (link "About")
                        (spacer)
                        (button {:on-click (fn [_] (js/alert "You're out!"))} "Logout"))
                (fixed-layout
                 (row
                  (column-9
                   (panel (header (title3 (:panel-title cursor)))
                          (p (:panel-text cursor))
                          (stretch
                           (h4 "This is it!!"))))
                  (fc/column-3* {:size :md}
                   (navpanel (header (title1 "Mnu"))
                             (link {:href "#"
                                    :on-click (fn [_] (js/alert "Uno!"))} "Uno")
                             (link "Dos")))))))

(fc/root page app-state {:target (. js/document (getElementById "app"))
                         :state {:texto "Hey you, hey me..."}})
