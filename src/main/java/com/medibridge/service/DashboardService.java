package com.medibridge.service;
import java.util.Map;
public interface DashboardService {
    Map<String, Object> getUserDashboard(Long userId);
    Map<String, Object> getAdminDashboard();
}
