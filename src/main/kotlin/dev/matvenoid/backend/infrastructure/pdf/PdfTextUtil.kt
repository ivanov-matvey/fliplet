package dev.matvenoid.backend.infrastructure.pdf

import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import org.springframework.stereotype.Service

@Service
class PdfTextUtil {
    private val chunkSize = 75_000

    private fun extract(pdf: ByteArray): String =
    Loader.loadPDF(pdf).use { doc ->
        val stripper = PDFTextStripper()
        (1..doc.numberOfPages).joinToString("\n") { page ->
            stripper.startPage = page
            stripper.endPage = page
            stripper.getText(doc).trim()
        }
    }

    fun splitForLlm(pdf: ByteArray): List<String> {
        val text = extract(pdf)
        return buildList {
            var start = 0
            while (start < text.length) {
                val end = (start + chunkSize).coerceAtMost(text.length)
                val safe = text.lastIndexOf('\n', end).takeIf { it >= start } ?: end
                add(text.substring(start, safe))
                start = safe + 1
            }
        }
    }
}