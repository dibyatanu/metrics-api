package uk.claritygroup.kotlin.exception

import java.lang.RuntimeException

class BadRequestException(
    message: String
): RuntimeException(message)
