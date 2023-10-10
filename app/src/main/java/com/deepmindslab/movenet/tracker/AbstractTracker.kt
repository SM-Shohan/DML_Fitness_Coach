package com.deepmindslab.movenet.tracker

import com.deepmindslab.movenet.body_parts_detection_data.Person


abstract class AbstractTracker(val config: TrackerConfig) {

    private val maxAge = config.maxAge * 1000 // convert milliseconds to microseconds
    private var nextTrackId = 0
    var tracks = mutableListOf<Track>()
        private set


    abstract fun computeSimilarity(persons: List<Person>): List<List<Float>>

    fun apply(persons: List<Person>, timestamp:Long):List<Person>{
        tracks= filterOldStack(timestamp).toMutableList()
        val simMatrix=computeSimilarity(persons)
        assignTrack(persons,simMatrix,timestamp)
        tracks=updateTrack().toMutableList()
        return persons
    }

    private  fun nextTrackID() = ++nextTrackId

    fun reset() {
        tracks.clear()
    }

    private fun assignTrack(persons: List<Person>, simMatrix:List<List<Float>>, timestamp:Long){

        if ((simMatrix.size != persons.size) != (simMatrix[0].size != tracks.size)) {
            throw IllegalArgumentException(
                "Size of person array and similarity matrix does not match.")
        }

        val unmatchedTrackIndices= MutableList(tracks.size){it}
        val unmatchedDetectionIndices= mutableListOf<Int>()

        for (detectionIndex in persons.indices){

            if (unmatchedTrackIndices.isEmpty()){
                unmatchedDetectionIndices.add(detectionIndex)
            }
            var maxTrackIndex = -1
            var maxSimilarity = -1f

            unmatchedTrackIndices.forEach { trackIndex ->
                val similarity = simMatrix[detectionIndex][trackIndex]
                if (similarity >= config.minSimilarity && similarity > maxSimilarity) {
                    maxTrackIndex = trackIndex
                    maxSimilarity = similarity
                }
            }
            if (maxTrackIndex >= 0){
                val linkedTrack = tracks[maxTrackIndex]
                tracks[maxTrackIndex] = createTrack(persons[detectionIndex],linkedTrack.person.id, timestamp)
                persons[detectionIndex].id = linkedTrack.person.id
                val index = unmatchedTrackIndices.indexOf(maxTrackIndex)
                unmatchedTrackIndices.removeAt(index)
            }
            else{
                unmatchedDetectionIndices.add(detectionIndex)
            }
        }

        unmatchedDetectionIndices.forEach {detectionIndex->
            val newTrack = createTrack(persons[detectionIndex], timestamp = timestamp)
            tracks.add(newTrack)
            persons[detectionIndex].id=newTrack.person.id
        }

    }

    private  fun updateTrack():List<Track>{
        tracks.sortByDescending { it.lastTimestamp }
        return tracks.take(config.maxTracks)
    }

    private fun  filterOldStack(timestamp: Long):List<Track>{
        return  tracks.filter {
            timestamp-it.lastTimestamp<=maxAge
        }
    }

    private fun createTrack(person: Person, id: Int? = null, timestamp: Long): Track {
        return Track(
            person = Person(
                id = id ?: nextTrackID(),
                keyPoints = person.keyPoints,
                boundingBox = person.boundingBox,
                score = person.score
            ),
            lastTimestamp = timestamp
        )
    }

}