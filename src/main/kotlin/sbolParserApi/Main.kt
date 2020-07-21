package sbolParserApi

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.apache.log4j.BasicConfigurator
import org.sbolstandard.core2.SBOLReader
import java.io.File

// Main server
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(CORS) {
        anyHost()
        method(HttpMethod.Options)
        allowNonSimpleContentTypes = true
    }
    // JSON / GSON conversion
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableHtmlEscaping()
        }
    }
    routing {
        get("/") {
            call.respondText("Hello World", ContentType.Text.Html)
        }

        post("/upload") {
            // Receive data
            val multiPartData = call.receiveMultipart()

            var fileContentType: ContentType
            var fileBytes = byteArrayOf()
            val propMaxByteSize = 1024 * 1024
            multiPartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        if (part.headers["Content-Length"]?.toInt() ?: 0 > propMaxByteSize) { // Check data size
                            call.respond(HttpStatusCode.PayloadTooLarge, "File is too large")
                        } else {
                            // Receive data
                            part.streamProvider().use { input -> fileBytes = input.readBytes() }

                            if (fileBytes.size > propMaxByteSize) { // Check data size again
                                call.respond(HttpStatusCode.BadRequest, "Content-Length header incorrect and file is too large")
                            } else {
                                fileContentType = part.contentType ?: ContentType.Application.Any
                                println(fileContentType.toString())
                                println(fileBytes.size)
//                                println(String(fileBytes))
                                parserSBOL(fileBytes)
                                call.respond(HttpStatusCode.OK, String(fileBytes))
                            }
                        }
                    }
                    else -> {
                        call.respond(HttpStatusCode.BadRequest, "Not a file upload")
                    }
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    BasicConfigurator.configure()
    embeddedServer(Netty, 8080, module = Application::module).start()
}


fun parserSBOL(fileBytes: ByteArray): Boolean {
    val filePath = "./examples/sbol_files/dummy.xml"
    val prURI = "http://www.dummy.org/"
    val combinatorialDerivationURI = "http://www.dummy.org/Trp_Optimization_CombinatorialDerivation/1"
//    val xmlFile = File(this.getCahce)
//    val doc = SBOLReader.read(fileBytes.inputStream())
    val doc = SBOLReader.read(filePath)

    doc.defaultURIprefix = prURI
    try {
        val parser = SBOLParser()
        parser.generateCsv(doc, combinatorialDerivationURI)
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}
