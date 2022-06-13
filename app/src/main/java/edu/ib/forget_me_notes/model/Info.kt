package edu.ib.forget_me_notes.model

class Info {

    private var infoname: String = ""
    private var description: String = ""
    private var place: String = ""
    private var watering: String = ""
    private var fertilizer: String = ""
    private var soil: String = ""
    private var image: String = ""
    private var iid: String = ""

    constructor()

    constructor(
        infoname: String,
        description: String,
        place: String,
        watering: String,
        fertilizer: String,
        soil: String,
        image: String,
        iid: String
    ) {
        this.infoname = infoname
        this.description = description
        this.place = place
        this.watering = watering
        this.fertilizer = fertilizer
        this.soil = soil
        this.image = image
        this.iid = iid
    }

    fun getInfoname(): String {
        return infoname
    }

    fun getDescription(): String {
        return description
    }

    fun getPlace(): String {
        return place
    }

    fun getWatering(): String {
        return watering
    }

    fun getFertilizer(): String {
        return fertilizer
    }

    fun getSoil(): String {
        return soil
    }

    fun getImage(): String {
        return image
    }

    fun getIid(): String {
        return  iid
    }

    fun setInfoname(infoname: String) {
        this.infoname = infoname
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun setPlace(place: String) {
        this.place = place
    }

    fun setWatering(watering: String) {
        this.watering = watering
    }

    fun setNFertilizer(fertilizer: String) {
        this.fertilizer = fertilizer
    }

    fun setSoil(soil: String) {
        this.soil = soil
    }

    fun setImage(image: String) {
        this.image = image
    }

    fun setIid(iid: String) {
        this.iid = iid
    }
}