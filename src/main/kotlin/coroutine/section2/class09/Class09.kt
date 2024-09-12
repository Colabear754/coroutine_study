package coroutine.section2.class09

import kotlinx.coroutines.delay

interface Continuation {
    suspend fun resumeWith(data: Any?)
}

class UserService {
    private val userProfileRepository = UserProfileRepository()
    private val userImageRepository = UserImageRepository()

    private abstract class FindUserContinuation : Continuation {
        var label = 0
        lateinit var profile: Profile
        lateinit var image: Image
    }

    suspend fun findUser(userId: Long, continuation: Continuation? = null): UserDto {
        val stateMachine = continuation as? FindUserContinuation ?: object : FindUserContinuation() {
            override suspend fun resumeWith(data: Any?) {
                when (label) {
                    0 -> {
                        profile = data as Profile
                        label = 1
                    }
                    1 -> {
                        image = data as Image
                        label = 2
                    }
                }
                findUser(userId, this)
            }
        }

        stateMachine.run { when (label) {
            0 -> {
                println("유저를 가져오겠습니다")
                userProfileRepository.findProfile(userId, this)
            }
            1 -> {
                println("이미지를 가져오겠습니다")
                userImageRepository.findImage(profile, this)
            }
        } }

        return UserDto(stateMachine.profile, stateMachine.image)
    }
}

data class UserDto(
    val profile: Profile,
    val image: Image,
)

class UserProfileRepository {
    suspend fun findProfile(userId: Long, continuation: Continuation) {
        delay(100L)
        continuation.resumeWith(Profile())
    }
}

class Profile
class UserImageRepository {
    suspend fun findImage(profile: Profile, continuation: Continuation) {
        delay(100L)
        continuation.resumeWith(Image())
    }
}

class Image