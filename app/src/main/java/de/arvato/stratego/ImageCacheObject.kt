package de.arvato.stratego

class ImageCacheObject {
    @JvmField
	var _piece = 0
    @JvmField
	var _color = 0
    @JvmField
	var _fieldColor = 0
    @JvmField
	var _bPiece = false
    @JvmField
	var _selectedPos = false
    @JvmField
	var _selected = false
    @JvmField
	var _coord: String? = null
    @JvmField
    var boardField = 0;

    companion object {
        @JvmField
		var _flippedBoard = false
    }
}