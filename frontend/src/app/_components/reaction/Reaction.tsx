'use client';

import { useState } from 'react';

import CommentIcon from '/public/icons/comment.svg';
import HeartIcon from '/public/icons/heart.svg';
import Comments from './Comments';

export default function Reaction({
  placeId,
  initialLiked,
}: {
  placeId: number;
  initialLiked: boolean;
}) {
  const [liked, setLiked] = useState(initialLiked);
  const [isOpen, setIsOpen] = useState(false);

  return (
    <div className="flex flex-col">
      <div className="flex">
        <button
          className={`py-2 flex gap-2 justify-center items-center grow ${
            isOpen && 'bg-black'
          }`}
          onClick={() => setIsOpen(!isOpen)}
        >
          <CommentIcon
            className={`w-6 h-6 ${isOpen ? 'stroke-white' : 'stroke-gray-500'}`}
          />
          <span
            className={`font-light ${isOpen ? 'text-white' : 'text-gray-500'}`}
          >
            댓글
          </span>
        </button>
        <button
          className="py-2 flex gap-2 justify-center items-center grow"
          onClick={() => setLiked(!liked)}
        >
          <HeartIcon
            className={`w-6 h-6 stroke-2 ${
              liked ? 'stroke-red-500 fill-red-500' : 'stroke-gray-500'
            }`}
          />
          <span
            className={`font-light ${liked ? 'text-black' : 'text-gray-500'}`}
          >
            좋아요
          </span>
        </button>
      </div>

      {isOpen && <Comments placeId={placeId} />}
    </div>
  );
}
