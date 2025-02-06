from django.urls import path
from .views import LoginUserView, RegisterUserView, PersonalPageView, CheckPointView, UserPointsView
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

urlpatterns = [
    path('register/', RegisterUserView.as_view(), name='register'),
    path('personal/', PersonalPageView.as_view(), name='personal'),
    path('login/', LoginUserView.as_view(), name='login'),
    path('token/', TokenObtainPairView.as_view(), name='token_obtain_pair'),
    path('token/refresh/', TokenRefreshView.as_view(), name='token_refresh'),
    path('check-point/', CheckPointView.as_view(), name='check_point'),
    path('points/', UserPointsView.as_view(), name='user_points'),
]
