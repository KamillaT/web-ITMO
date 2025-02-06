from django.contrib.auth.models import AbstractUser
from django.db import models

class CustomUser(AbstractUser):
    email = models.EmailField(unique=True)
    name = models.CharField(max_length=255, blank=True)
    username = models.CharField(max_length=150, unique=True, blank=False)
    
    USERNAME_FIELD = 'email'
    REQUIRED_FIELDS = []

    def __str__(self):
        return self.email


class Point(models.Model):
    user = models.ForeignKey(CustomUser, on_delete=models.CASCADE, related_name='points')
    x = models.FloatField()
    y = models.FloatField()
    r = models.FloatField()
    is_inside = models.BooleanField()
    execution_time = models.FloatField()
    created_at = models.DateTimeField(auto_now_add=True)
