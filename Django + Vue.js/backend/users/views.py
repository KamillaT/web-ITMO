from django.shortcuts import render
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework import status
from .models import CustomUser, Point
from .serializers import UserSerializer, PointSerializer
from django.contrib.auth import authenticate
import time


class RegisterUserView(APIView):
    def post(self, request):
        serializer = UserSerializer(data=request.data)
        if serializer.is_valid():
            serializer.save()
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)


class PersonalPageView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        if not request.user.is_authenticated:
            return Response({'error': 'User is not authenticated'}, status=401)
        
        user = request.user
        if not user:
            return Response({'error': 'User not found'}, status=404)
        
        return Response({
            "email": user.email,
            "username": user.username,
        })
            

class LoginUserView(APIView):
    def post(self, request):
        email = request.data.get('email')
        password = request.data.get('password')

        if not email or not password:
            return Response({'error': 'Email and password are required.'}, status=status.HTTP_400_BAD_REQUEST)

        user = authenticate(username=email, password=password)
        if user is not None:
            return Response({
                'email': user.email,
                'username': user.username,
            }, status=status.HTTP_200_OK)
        else:
            return Response({'error': 'Invalid email or password.'}, status=status.HTTP_401_UNAUTHORIZED)


class CheckPointView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request):
        try:
            x = float(request.data.get('x'))
            y = float(request.data.get('y'))
            r = float(request.data.get('r'))

            # Validate input ranges
            if not (-5 <= x <= 5):
                return Response({'error': 'X must be between -5 and 5'}, status=status.HTTP_400_BAD_REQUEST)
            if not (-5 <= y <= 3):
                return Response({'error': 'Y must be between -5 and 3'}, status=status.HTTP_400_BAD_REQUEST)
            if not (0 < r <= 5):
                return Response({'error': 'R must be between 0 and 5'}, status=status.HTTP_400_BAD_REQUEST)

            # Calculate execution time
            start_time = time.time()

            # Determine if the point is inside the region
            is_inside = (
                (x >= 0 and y >= 0 and x**2 + y**2 <= (r / 2)**2) or
                (x >= 0 and y <= 0 and x <= r and y >= -r) or
                (x <= 0 and y >= 0 and y <= r and x >= -r / 2)
            )

            end_time = time.time()
            execution_time = end_time - start_time

            # Create and save the point
            point = Point.objects.create(
                user=request.user,
                x=x,
                y=y,
                r=r,
                is_inside=is_inside,
                execution_time=execution_time
            )

            # Serialize and return the response
            serializer = PointSerializer(point)
            return Response(serializer.data, status=status.HTTP_201_CREATED)

        except ValueError:
            return Response({'error': 'Invalid data format'}, status=status.HTTP_400_BAD_REQUEST)


class UserPointsView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request):
        user = request.user
        points = Point.objects.filter(user=user).order_by('-created_at')
        if points.exists():
            serializer = PointSerializer(points, many=True)
            return Response(serializer.data)
        else:
            return Response({'message': 'No points found for this user.'}, status=status.HTTP_200_OK)
