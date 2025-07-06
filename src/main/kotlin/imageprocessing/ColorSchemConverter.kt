package imageprocessing

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp

/**
 * Интерфейс с методом преобразования цветов изображения
 */
interface ColorSchemConverter {
    /**
     * Преобразует одно изображение в другое, изменяя цвет
     * @param image входное изображение
     * @return преобразованное изображение
     */
    abstract fun convert(image: BufferedImage): BufferedImage
}

/**
 * Конвертер в оттенки серого
 */
class GrayColorSchemeConverter: ColorSchemConverter {
    /**
     * Преобразует изображение в изображение в оттенках серого
     * @param image исходное изображение
     * @return копию изображения, в оттенках серого
     */
    override fun convert(image: BufferedImage): BufferedImage {
        val gray = BufferedImage(image.width,image.height, BufferedImage.TYPE_BYTE_GRAY)
        val xformOp = ColorConvertOp(null)
        xformOp.filter(image,gray)
        return gray
    }
}

/**
 * Конвертер в бинарное черно-белое изображение
 * @param threshold граница яркости для преобразования в черный или белый цвет
 */
class BinaryColorSchemeConverter(val threshold: Int): ColorSchemConverter {
    /**
     * Преобразует изображение в изображение в оттенках серого
     * @param image исходное изображение
     * @return копию изображения, в оттенках серого
     */
    override fun convert(image: BufferedImage): BufferedImage {
            val image = image
            val binaryRaster = image.getData()
            val pix = IntArray(4)
            for(i in 0..<image.width) {
                for (j in 0..<image.height) {
                    binaryRaster.getPixel(i, j, pix)
                    if (pix[1] != 0) {
                        println(pix[1])
                    }
                    if (pix[0] > threshold) {
                        image.setRGB(i, j, Color.WHITE.getRGB())
                    } else {
                        image.setRGB(i, j, Color.BLACK.getRGB())
                    }
                }

            }
        return image
    }

}