//package com.wings.mile.mapbox
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.mapbox.maps.MapView
//import com.mapbox.maps.Style
//import com.mapbox.maps.plugin.locationcomponent.location
//import com.wings.mile.R
//
//class MapsActivity : AppCompatActivity() {
//    var mapView: MapView? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
//        mapView = findViewById(R.id.mapView)
//        //mapView?.getMapboxMap()?.loadStyleUri(Style.TRAFFIC_DAY)
//
//        mapView?.getMapboxMap()?.loadStyleUri(
//            Style.MAPBOX_STREETS,
//            // After the style is loaded, initialize the Location component.
//            object : Style.OnStyleLoaded {
//                override fun onStyleLoaded(style: Style) {
//                    mapView?.location?.updateSettings {
//                        enabled = true
//                        pulsingEnabled = true
//                    }
//                }
//            }
//        )
//    }
//
//    override fun onStart() {
//        super.onStart()
//        mapView?.onStart()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        mapView?.onStop()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mapView?.onLowMemory()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mapView?.onDestroy()
//    }
//}