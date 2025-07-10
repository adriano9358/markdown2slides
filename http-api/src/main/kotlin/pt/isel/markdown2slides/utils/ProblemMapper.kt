package pt.isel.markdown2slides.utils

import pt.isel.markdown2slides.ConversionError
import pt.isel.markdown2slides.ProjectError
import pt.isel.markdown2slides.model.Problem

fun ProjectError.toProblem(): Problem = when (this) {
    is ProjectError.ContentDeletionError -> Problem.ContentDeletionError
    is ProjectError.ImageDeletionError -> Problem.ImageDeletionError
    is ProjectError.ImageNotFound -> Problem.ImageNotFound
    is ProjectError.ProjectNotFound -> Problem.ProjectNotFound
    is ProjectError.ProjectNotAuthorized -> Problem.ProjectNotAuthorized
    is ProjectError.DatabaseError -> Problem.DatabaseError
    is ProjectError.FileSystemError -> Problem.FileSystemError
    is ProjectError.UnknownError -> Problem.UnknownError
    is ProjectError.InvalidInvitationResponse -> Problem.InvalidInvitationResponse
    is ProjectError.InvalidRole -> Problem.InvalidRole
    is ProjectError.InvitationAlreadyExists -> Problem.InvitationAlreadyExists
    is ProjectError.InvitationAlreadyResponded -> Problem.InvitationAlreadyResponded
    is ProjectError.InvitationNotFound -> Problem.InvitationNotFound
    is ProjectError.InviterNotAuthorized -> Problem.InviterNotAuthorized
    is ProjectError.InviterNotFound -> Problem.InviterNotFound
    is ProjectError.UserAlreadyInProject -> Problem.UserAlreadyInProject
    is ProjectError.UserNotFound -> Problem.UserNotFound
    is ProjectError.UserNotInProject -> Problem.UserNotInProject
    is ProjectError.InvalidUpdates -> Problem.InvalidUpdates
    is ProjectError.VersionMismatch -> Problem.VersionMismatch
}

fun ConversionError.toProblem(): Problem = when (this) {
    ConversionError.SomeConversionError -> Problem.ConversionProcessFailure
    ConversionError.PandocError -> Problem.PandocError
}