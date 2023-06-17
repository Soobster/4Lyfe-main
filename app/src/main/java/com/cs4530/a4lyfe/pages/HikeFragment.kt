package com.cs4530.a4lyfe.pages

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.cs4530.a4lyfe.models.decodeHikeData
import com.cs4530.a4lyfe.ObserverFragment
import com.cs4530.a4lyfe.R
import com.cs4530.a4lyfe.models.HikeData
import com.cs4530.a4lyfe.models.viewModels.HikeViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.json.JSONException


class HikeFragment : ObserverFragment<String>(), OnMapReadyCallback {
    private var googleMap: GoogleMap? = null
    private var loader: ProgressBar? = null
    private var mapView: MapView? = null
    private var hikesForMapReady: String? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        val parent = this
        lifecycleScope.launch(Dispatchers.Main) {
            whenStarted {
                assert(activityInterface != null)
                activityInterface?.repo?.mUserDao?.allHikes?.collect {
                    launch(Dispatchers.IO) {
                        if (it != null) {
                            ViewModelProvider(parent).get(
                                HikeViewModel::class.java
                            ).hikesData.value = it
                        } else {
                            for (i in 0..5) {
                                val loc = activityInterface!!.getLocation()
                                if (loc == null) {
                                    delay(1000)
                                } else {


                                    ViewModelProvider(parent).get(
                                        HikeViewModel::class.java
                                    ).setHikeLocation(
                                        parent.requireContext(),
                                        loc
                                    )
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_hike, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = view.findViewById<View>(R.id.google_map) as MapView
        loader = view.findViewById(R.id.hiking_loader)
    }

    override fun onMapReady(map: GoogleMap) {
        MapsInitializer.initialize(requireContext())
        googleMap = map
        googleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        loader!!.visibility = View.GONE
        try {
            val hikes = convertStringToListHike(hikesForMapReady)
            if (hikes != null) {
                for(hike in hikes) {
                    if(hike.geometry != null && hike.geometry!!.location != null && hike.geometry!!.location!!.lng != null &&  hike.geometry!!.location!!.lat != null && hike.name != null) {
                        googleMap!!.addMarker(
                            MarkerOptions()
                                .position(LatLng(hike.geometry!!.location!!.lat!!, hike.geometry!!.location!!.lng!!))
                                .title(hike.name)
                        )
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        assert(activityInterface != null)
        val loc = activityInterface!!.getLocation()

        val target = CameraPosition.builder().target(LatLng(loc!!.latitude, loc.longitude))
            .zoom(12f).bearing(0f).tilt(45f).build()
        googleMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(target))
    }

    companion object {
        fun newInstance(): HikeFragment {
            return HikeFragment()
        }
    }

    override suspend fun setPageElements(objectToObserve: String?) {
        val parent = this
        lifecycleScope.launch(Dispatchers.Main) {
            if (objectToObserve != null) {
                hikesForMapReady = objectToObserve
                mapView!!.onCreate(null)
                mapView!!.onResume()
                mapView!!.getMapAsync(parent)
            }
        }
    }

    override suspend fun observeDataBootstrap(data: String?) {
        val liveData = ViewModelProvider(this).get(
            HikeViewModel::class.java
        ).hikesData
        liveData.observe(viewLifecycleOwner, observer)
        liveData.value?.let {
            hikesForMapReady =it
            setPageElements(it)
        }
    }

    private fun convertStringToListHike(hikeString: String?): List<HikeData>? {
        if(hikeString != null) {
            val hikeListSerializer = ListSerializer(HikeData.serializer())
            return Json { ignoreUnknownKeys = true }.decodeFromString(
                hikeListSerializer,
                hikeString
            )
        }
        return null

    }


}