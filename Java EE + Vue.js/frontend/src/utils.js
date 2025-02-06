export function handleApiError(error, showNotification) {
  if (error.response) {
    const status = error.response.status;

    if (status === 401) {
      showNotification("Your session expired. You will be logged out in several seconds");
      setTimeout(() => {
        localStorage.removeItem("authToken");
        window.location.href = "/";
      }, 5000);
    } else if (status === 404) {
      showNotification(error.response.data.error || "Resource not found.");
    } else if (status === 409) {
      showNotification(error.response.data.error || "Conflict occurred. Seems like this email is already registered");
    } else if (status === 400) {
      showNotification(error.response.data.error || "Bad request.");
    } else {
      showNotification("An unexpected error occurred. Please try again later.");
    }
  } else {
    showNotification("A network error occurred. Please check your connection and try again.");
  }

  console.error("API Error:", error);
}

