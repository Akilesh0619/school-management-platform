import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Users, GraduationCap, DollarSign, BookOpen, UserCheck, Activity } from 'lucide-react';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, BarElement, Title, Tooltip, Legend, ArcElement } from 'chart.js';
import { Line, Bar, Doughnut } from 'react-chartjs-2';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, Title, Tooltip, Legend, ArcElement);

export const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchStats();
  }, []);

  const fetchStats = async () => {
    try {
      const res = await api.get('/dashboard/stats');
      setStats(res.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="w-8 h-8 border-4 border-indigo-500/30 border-t-indigo-500 rounded-full animate-spin" />
      </div>
    );
  }

  const statCards = [
    { label: 'Total Students', value: stats?.totalStudents || 0, icon: GraduationCap, color: 'from-indigo-500 to-blue-500' },
    { label: 'Total Teachers', value: stats?.totalTeachers || 0, icon: Users, color: 'from-violet-500 to-purple-500' },
    { label: 'Total Parents', value: stats?.totalParents || 0, icon: UserCheck, color: 'from-emerald-500 to-teal-500' },
    { label: 'Total Classes', value: stats?.totalClasses || 0, icon: BookOpen, color: 'from-amber-500 to-orange-500' },
    { label: 'Total Fee Collected', value: `$${stats?.totalFeeCollected || 0}`, icon: DollarSign, color: 'from-rose-500 to-pink-500' },
  ];

  // Chart Data
  const attendanceChartData = {
    labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
    datasets: [
      {
        label: 'Attendance Ratio (%)',
        data: [95, 92, 98, 94, 91, 88],
        borderColor: '#6366f1',
        backgroundColor: 'rgba(99, 102, 241, 0.1)',
        tension: 0.4,
        fill: true,
      },
    ],
  };

  const financeChartData = {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun'],
    datasets: [
      {
        label: 'Income ($)',
        data: [12000, 15000, 18000, 14000, 20000, 22000],
        backgroundColor: '#10b981',
      },
      {
        label: 'Expense ($)',
        data: [5000, 6000, 7500, 4800, 8000, 7000],
        backgroundColor: '#ef4444',
      },
    ],
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div>
        <h1 className="text-2xl font-bold">Executive Analytics Dashboard</h1>
        <p className="text-sm text-slate-500">Real-time stats across academics, attendance, and finance.</p>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 lg:grid-cols-5 gap-4">
        {statCards.map((card, idx) => {
          const Icon = card.icon;
          return (
            <div key={idx} className="glass-card p-5 relative overflow-hidden group hover:scale-[1.02] transition-all duration-300">
              <div className={`w-12 h-12 rounded-xl bg-gradient-to-tr ${card.color} flex items-center justify-center text-white mb-3 shadow-lg`}>
                <Icon size={24} />
              </div>
              <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">{card.label}</p>
              <p className="text-2xl font-bold mt-1">{card.value}</p>
            </div>
          );
        })}
      </div>

      {/* Charts Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="glass-card p-6">
          <h2 className="text-lg font-bold mb-4 flex items-center space-x-2">
            <Activity className="text-indigo-500" size={20} />
            <span>Weekly Attendance Trend</span>
          </h2>
          <div className="h-64">
            <Line data={attendanceChartData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </div>

        <div className="glass-card p-6">
          <h2 className="text-lg font-bold mb-4 flex items-center space-x-2">
            <DollarSign className="text-emerald-500" size={20} />
            <span>Financial Income vs Expenses</span>
          </h2>
          <div className="h-64">
            <Bar data={financeChartData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </div>
      </div>

      {/* Audit Logs / Activity Stream */}
      <div className="glass-card p-6">
        <h2 className="text-lg font-bold mb-4">Recent System Activity Logs</h2>
        <div className="space-y-3 max-h-60 overflow-y-auto">
          {stats?.recentActivities?.map((act: any, idx: number) => (
            <div key={idx} className="flex items-center justify-between p-3 rounded-xl bg-slate-100/50 dark:bg-slate-800/50 border border-slate-200/50 dark:border-slate-700/50">
              <div>
                <span className="font-semibold text-sm">{act.username}</span>
                <span className="text-xs text-indigo-400 font-mono ml-2">[{act.action}]</span>
                <p className="text-xs text-slate-500 mt-0.5">{act.details}</p>
              </div>
              <span className="text-[10px] text-slate-400 font-mono">{act.timestamp?.split('T')?.[0]}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
