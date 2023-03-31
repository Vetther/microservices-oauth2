package pl.owolny.userservice.controller.request;

public record UpdateLinkRequest(
        String providerUserId,
        String providerUserEmail,
        String providerUserName
) {
}
