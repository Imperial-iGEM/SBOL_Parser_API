package sbolParserApi

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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.apache.log4j.BasicConfigurator
import org.sbolstandard.core2.SBOLReader
import java.io.File
import java.io.IOException

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
                                val constructCSV = File("./examples/sbol_files/constructs.csv")
                                val outputBytes = constructCSV.readBytes()
                                val contentType = "text/csv".toMediaTypeOrNull()
                                val fileConstruct = constructCSV
                                val partsLinkers = File("./examples/sbol_files/parts_linkers.csv")
                                val requestBody: RequestBody = MultipartBody.Builder()
                                        .setType(MultipartBody.FORM)
                                        .addFormDataPart("ethanol_stage2", "A1")
                                        .addFormDataPart("deep_well_stage4", "A11")
                                        .addFormDataPart("construct_csv", "constructs.csv", fileConstruct.asRequestBody(contentType))
                                        .addFormDataPart("parts_linkers_csv", "parts_linkers_csv", partsLinkers.asRequestBody(contentType))
                                        .build()
                                val client = OkHttpClient()

                                val request = Request.Builder().url("http://127.0.0.1:8000/Input/").post(requestBody).build()
                                try {
                                    val response: Response = client.newCall(request).execute()

                                    print(response.toString())
                                    if (response.code == 200) {
                                        response.body?.string()?.let { it1 -> print(it1); call.respond(HttpStatusCode.OK) }
                                    } else {
                                        call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
                                    }

                                    // Do something with the response.
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
                                }
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
    val filePath = "./examples/sbol_files/iGEM2020/Trp_Optimization.xml"
    val prURI = "http://www.dummy.org/"
    val combinatorialDerivationURI = "http://www.dummy.org/Trp_Optimization_CombinatorialDerivation/1"
//    val xmlFile = File(this.getCahce)
    println(KotlinVersion.CURRENT)
    println("v" + System.getProperty("java.version"))
    val doc = SBOLReader.read(fileBytes.inputStream())
//    val doc = SBOLReader.read(filePath)

    doc.defaultURIprefix = prURI
    try {
        val parser = SBOLParser()
        parser.generateCsv(doc, "COMBINATORIAL_DERIVATION", combinatorialDerivationURI, 88)
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
    return true
}
