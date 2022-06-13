package edu.ib.forget_me_notes.model

class Note {

    private var noteid : String = ""
    private var noteimage : String = ""
    private var publisher : String = ""
    private var name : String = ""
    private var nick : String = ""
    private var info : String = ""
    private var light : String = ""
    private var water : String = ""
    private var ground : String = ""

    constructor()

    constructor(
        noteid: String,
        noteimage: String,
        publisher: String,
        name: String,
        nick: String,
        info: String,
        light: String,
        water: String,
        ground: String
    ) {
        this.noteid = noteid
        this.noteimage = noteimage
        this.publisher = publisher
        this.name = name
        this.nick = nick
        this.info = info
        this.light = light
        this.water = water
        this.ground = ground
    }

    fun getNoteid(): String {
        return noteid
    }

    fun setNotetid(noteid: String) {
        this.noteid = noteid
    }

    fun getNoteimage(): String {
        return noteimage
    }

    fun setNoteimage(noteimage: String) {
        this.noteimage = noteimage
    }

    fun getPublisher(): String {
        return publisher
    }

    fun setPublisher(publisher: String) {
        this.publisher = publisher
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getNick(): String {
        return nick
    }

    fun setNick(nick: String) {
        this.nick = nick
    }

    fun getInfo(): String {
        return info
    }

    fun setInfo(info: String) {
        this.info = info
    }

    fun getLight(): String {
        return light
    }

    fun setLight(light: String) {
        this.light = light
    }

    fun getWater(): String {
        return water
    }

    fun setWater(water: String) {
        this.water = water
    }

    fun getGround(): String {
        return ground
    }

    fun setGround(ground: String) {
        this.ground = ground
    }
}