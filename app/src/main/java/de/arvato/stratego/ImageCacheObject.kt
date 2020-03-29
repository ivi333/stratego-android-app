package de.arvato.stratego

class ImageCacheObject {
    @JvmField
	var piece = 0
    @JvmField
	var color = 0
    @JvmField
	var fieldColor = 0
    @JvmField
	var bPiece = false
    @JvmField
	var selectedPos = false
    @JvmField
	var selected = false
    @JvmField
	var coord: String? = null
    @JvmField
    var boardField = 0;
    @JvmField
    var initalized = false;
    @JvmField
    var enemy = false;

    companion object {
        @JvmField
		var _flippedBoard = false
    }
}