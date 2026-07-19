import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  LayoutDashboard,
  GraduationCap,
  Users,
  UserCheck,
  BookOpen,
  Calendar,
  ClipboardList,
  FileText,
  DollarSign,
  Clock,
  Bell,
  Settings,
  Search,
  LogOut,
  ChevronLeft,
  ChevronRight
} from 'lucide-react';

interface SidebarProps {
  collapsed: boolean;
  setCollapsed: (v: boolean) => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ collapsed, setCollapsed }) => {
  const location = useLocation();
  const { user, logout, hasRole } = useAuth();

  const menuItems = [
    { label: 'Dashboard', icon: LayoutDashboard, path: '/dashboard', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: 'Students', icon: GraduationCap, path: '/students', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER'] },
    { label: 'Teachers', icon: Users, path: '/teachers', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN'] },
    { label: 'Parents', icon: UserCheck, path: '/parents', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN'] },
    { label: 'Academics', icon: BookOpen, path: '/academics', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER'] },
    { label: 'Attendance', icon: ClipboardList, path: '/attendance', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: 'Marks & Exams', icon: FileText, path: '/marks', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: 'Timetable', icon: Clock, path: '/timetable', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: 'Fees & Finance', icon: DollarSign, path: '/fees', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_PARENT', 'ROLE_STUDENT'] },
    { label: 'Notice Board', icon: Bell, path: '/notices', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: 'Events', icon: Calendar, path: '/events', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN', 'ROLE_TEACHER', 'ROLE_STUDENT', 'ROLE_PARENT'] },
    { label: 'Operations', icon: Settings, path: '/operations', roles: ['ROLE_SUPER_ADMIN', 'ROLE_ADMIN'] },
  ];

  return (
    <aside
      className={`fixed top-0 left-0 z-40 h-screen transition-all duration-300 glass-nav flex flex-col justify-between ${
        collapsed ? 'w-20' : 'w-64'
      }`}
    >
      <div>
        {/* Brand header */}
        <div className="h-16 flex items-center justify-between px-4 border-b border-slate-200/50 dark:border-slate-800/50">
          {!collapsed && (
            <div className="flex items-center space-x-3">
              <div className="w-10 h-10 rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center text-white font-bold text-xl shadow-lg shadow-indigo-500/30">
                S
              </div>
              <span className="font-bold text-lg bg-gradient-to-r from-indigo-500 to-violet-400 bg-clip-text text-transparent">
                EduMaster
              </span>
            </div>
          )}
          {collapsed && (
            <div className="w-10 h-10 mx-auto rounded-xl bg-gradient-to-tr from-indigo-600 to-violet-500 flex items-center justify-center text-white font-bold text-xl shadow-lg">
              S
            </div>
          )}
          <button
            onClick={() => setCollapsed(!collapsed)}
            className="p-1.5 rounded-lg hover:bg-slate-200/50 dark:hover:bg-slate-800/50 text-slate-500"
          >
            {collapsed ? <ChevronRight size={18} /> : <ChevronLeft size={18} />}
          </button>
        </div>

        {/* Menu Items */}
        <nav className="p-3 space-y-1 overflow-y-auto max-h-[calc(100vh-140px)]">
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center space-x-3 px-3 py-2.5 rounded-xl transition-all duration-200 font-medium ${
                  isActive
                    ? 'bg-gradient-to-r from-indigo-600 to-violet-600 text-white shadow-lg shadow-indigo-500/25'
                    : 'text-slate-600 dark:text-slate-400 hover:bg-slate-200/60 dark:hover:bg-slate-800/60 hover:text-slate-900 dark:hover:text-white'
                }`}
                title={collapsed ? item.label : undefined}
              >
                <Icon size={20} className={isActive ? 'text-white' : 'text-slate-500'} />
                {!collapsed && <span>{item.label}</span>}
              </Link>
            );
          })}
        </nav>
      </div>

      {/* Footer / Profile snapshot */}
      <div className="p-3 border-t border-slate-200/50 dark:border-slate-800/50">
        <div className={`flex items-center ${collapsed ? 'justify-center' : 'justify-between'} px-2 py-2`}>
          {!collapsed && (
            <div className="truncate">
              <p className="font-semibold text-sm truncate">{user?.username}</p>
              <p className="text-xs text-slate-500 truncate">{user?.roles?.[0] || 'User'}</p>
            </div>
          )}
          <button
            onClick={logout}
            className="p-2 rounded-xl text-red-500 hover:bg-red-500/10 transition-colors"
            title="Logout"
          >
            <LogOut size={18} />
          </button>
        </div>
      </div>
    </aside>
  );
};
