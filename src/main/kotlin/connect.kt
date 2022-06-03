import org.litote.kmongo.KMongo

val client = KMongo
    .createClient("mongodb://root:hP66lWlM062Z@192.168.1.47:27017")
val mongoDatabase = client.getDatabase("test")