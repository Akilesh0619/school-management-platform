import React, { useEffect, useState } from 'react';
import api from '../api/client';
import { Calendar as CalendarIcon, Bell } from 'lucide-react';

export const NoticeAndEvents: React.FC = () => {
  const [notices, setNotices] = useState<any[]>([]);
  const [events, setEvents] = useState<any[]>([]);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [resN, resE] = await Promise.all([
        api.get('/notices'),
        api.get('/events'),
      ]);
      setNotices(resN.data);
      setEvents(resE.data);
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
      {/* Notice Board */}
      <div className="glass-card p-6 space-y-4">
        <h2 className="text-xl font-bold flex items-center space-x-2">
          <Bell className="text-indigo-400" size={22} />
          <span>Notice Board Bulletin</span>
        </h2>
        <div className="space-y-4 max-h-[500px] overflow-y-auto pr-2">
          {notices.map((n) => (
            <div key={n.id} className="p-4 rounded-xl bg-slate-800/40 border border-slate-700/50 space-y-2">
              <div className="flex justify-between items-center">
                <h3 className="font-bold text-base">{n.title}</h3>
                <span className="text-[10px] px-2 py-0.5 rounded bg-indigo-500/10 text-indigo-400 font-bold uppercase">
                  {n.targetAudience}
                </span>
              </div>
              <p className="text-sm text-slate-300">{n.content}</p>
              <div className="text-[11px] text-slate-500 pt-2 flex justify-between">
                <span>Posted by {n.createdByName}</span>
                <span>{n.createdAt?.split('T')?.[0]}</span>
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Events Calendar */}
      <div className="glass-card p-6 space-y-4">
        <h2 className="text-xl font-bold flex items-center space-x-2">
          <CalendarIcon className="text-emerald-400" size={22} />
          <span>School Calendar & Events</span>
        </h2>
        <div className="space-y-4 max-h-[500px] overflow-y-auto pr-2">
          {events.map((e) => (
            <div key={e.id} className="p-4 rounded-xl bg-slate-800/40 border border-slate-700/50 space-y-2">
              <div className="flex justify-between items-center">
                <h3 className="font-bold text-base">{e.title}</h3>
                <span className="text-[10px] px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-400 font-bold uppercase">
                  {e.type}
                </span>
              </div>
              <p className="text-sm text-slate-300">{e.description}</p>
              <p className="text-xs text-slate-400 font-mono">
                From: {e.startTime?.split('T')?.[0]} to {e.endTime?.split('T')?.[0]}
              </p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
