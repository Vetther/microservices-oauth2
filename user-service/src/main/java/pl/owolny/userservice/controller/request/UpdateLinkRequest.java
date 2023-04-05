package pl.owolny.userservice.controller.request;

public record UpdateLinkRequest(
        String providerUserEmail,
        String providerUserName
) {
}
