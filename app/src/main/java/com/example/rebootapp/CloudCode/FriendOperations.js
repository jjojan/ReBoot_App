async function fetchFriends(user){
    const friendsRelation = user.relation("friends");
    const friendsQuery = friendsRelation.query();
    const friends = await friendsQuery.find({ useMasterKey: true });
    return friends;
}

async function fetchBlocked(user){
    const blockedQuery = new Parse.Query("Blocked");
    blockedQuery.equalTo("source_user", user);
    blocked = await blockedQuery.find({useMasterKey: true});
    const blockedIds = blocked.map(b => b.get("blocked_user").id);
    return blockedIds;

}

async function fetchBlockedUsers(user){
    const blockedQuery = new Parse.Query("Blocked");
    blockedQuery.equalTo("source_user", user);
    blockedQuery.include("blocked_user");
    blocked = await blockedQuery.find({useMasterKey: true});


    let rDetails = [];

    for (let detail of blocked){
        const requestUser = detail.get("blocked_user");
        const mutFriendCount = await countMutualFriends(user, requestUser);
        rDetails.push({user: requestUser, mutualFriendsCount: mutFriendCount});
    }

    return rDetails;
}

async function fetchBlocker(user){
    const blockedQuery = new Parse.Query("Blocked");
    blockedQuery.equalTo("blocked_user", user);
    blocked = await blockedQuery.find({useMasterKey: true});
    const blockedIds = blocked.map(b => b.get("source_user").id);
    return blockedIds;
}

async function countMutualFriends(user1, user2){
    const fq1 = user1.relation("friends").query();
    const friends1 = await fq1.find({useMasterKey: true});
    const friends1Ids = friends1.map(friend => friend.id);

    const fq2 = user2.relation("friends").query();
    const friends2 = await fq2.find({useMasterKey: true});
    const friends2Ids = friends2.map(friend => friend.id);


    const mutFriendIds = friends1Ids.filter(id => friends2Ids.includes(id));
    return mutFriendIds.length;

}

async function fetchFriendsOfFriends(user, friendIds) {
    const fofQuery = new Parse.Query(Parse.User); // query user class
    const friendsQuery = user.relation("friends").query(); //direct friends of user as a query
    fofQuery.matchesQuery("friends", friendsQuery); // Friends-of-friends
    // fofQuery.notContainedIn("objectId", friendIds); // Exclude direct friends
    const fof = await fofQuery.find({ useMasterKey: true });
    return fof;
}

async function fetchRequestedFriends(user){
    const rQuery = new Parse.Query("FriendRequest");
    rQuery.equalTo("destination_user", user);
    rQuery.include("source_user");
    results = await rQuery.find({useMasterKey: true});

    let rDetails = [];

    for (let detail of results){
        const requestUser = detail.get("source_user");
        const mutFriendCount = await countMutualFriends(user, requestUser);
        rDetails.push({user: requestUser, mutualFriendsCount: mutFriendCount});
    }

    return rDetails;
}

async function FetchFriendsOfFriends2(user, userId) {
    const blocked = await fetchBlocked(user);
    const blocker = await fetchBlocker(user);

    const friendsQuery = user.relation("friends").query();
    const friendIds = (await friendsQuery.find({useMasterKey: true})).map(friend => friend.id);
    const fofQuery = new Parse.Query(Parse.User);


    fofQuery.matchesQuery("friends", friendsQuery);

    const excludeIds = friendIds.concat(userId).concat(blocked).concat(blocker);

    fofQuery.notContainedIn("objectId", excludeIds);

    // fofQuery.notContainedIn("objectId", friendIds.concat(userId));
    // fofQuery.notContainedIn("objectId", blocked);

    const fofResults = await fofQuery.find({useMasterKey: true});
    let foFDetails = [];

    for (let friend of fofResults){
        const count = await countMutualFriends(user, friend);
        foFDetails.push({user: friend, mutualFriendsCount: count});
    }

    return foFDetails;


}


async function FetchFriendsOfFriendsNonMutual(user){
    const fofQuery = new Parse.Query(Parse.User);
    const friends = await fetchFriends(user);
    const friendIds  = friends.map(friend => friend.id);



    let foFDetails = [];

    for (let friend of friends){

        const eachFriendFriends = await fetchFriends(friend);

        for (let f of eachFriendFriends){
            if (!friendIds.includes(f.id) && f.id !== user.id){
                let detail = foFDetails.find(detail => detail.user.id === f.id);

                if(detail){
                    detail.mutualFriendsCount++;
                }
                else {
                    foFDetails.push({user: f, mutualFriendsCount : 1});
                }
            }
        }
    }
    return foFDetails;
    // const finalQuery = new Parse.Query(Parse.User);
    // finalQuery.containedIn("objectId", Array.from(foFIds));
    // const fofUsers = await finalQuery.find({useMasterKey: true});
    // return fofUsers;
}

async function getUserByUsername(username) {
    const query = new Parse.Query(Parse.User);
    query.equalTo("username", username);
    const user = await query.first({ useMasterKey: true });
    return user;
}

async function getUserById(userId){
    const query = new Parse.Query(Parse.User);
    query.equalTo("objectId", userId);
    const user = await query.first({useMasterKey: true});
    return user;
}

async function getReviewById(reviewId){
    const review = Parse.Object.extend("Review");
    const reviewQuery = new Parse.Query(review);
    requestQuery.equalTo("objectId", reviewId);
    const ans = await reviewQuery.first({useMasterKey: true});
    return ans;
}



async function addFriend(user1, user2){
    const relation = user1.relation("friends");
    const query = relation.query();
    query.equalTo("objectId", user2.id);

    const results = await query.find({useMasterKey: true});

    if(results.length === 0){
        relation.add(user2);
        await user1.save(null, {useMasterKey: true});
        return "Friend added";
    }
    else {
        return "Friend already added";
    }

}

async function removeFriend(user1, user2) {
    const relation = user1.relation("friends");
    const query = relation.query();
    query.equalTo("objectId", user2.id);

    const results = await query.find({useMasterKey: true});

    if (results.length > 0) {
        // If user2 is found in user1's friends, remove user2 from the relation
        relation.remove(user2);
        await user1.save(null, {useMasterKey: true});
        return "Friend removed";
    } else {
        // If user2 is not found in user1's friends, nothing to remove
        return "Friend not found";
    }
}

async function addFriendRequest(user1, user2){

    const relation = user1.relation("friends");
    const query = relation.query();
    query.equalTo("objectId", user2.id);

    const results = await query.find({useMasterKey: true});

    if(results.length > 0){
        return "Friend already added"
    }

    const FriendRequest = Parse.Object.extend("FriendRequest");
    const requestQuery = new Parse.Query(FriendRequest);
    requestQuery.equalTo("source_user", user1);
    requestQuery.equalTo("destination_user", user2);

    const requestResults = await requestQuery.find({useMasterKey: true});

    if(requestResults.length === 0){
        const req = new FriendRequest();
        req.set("source_user", user1);
        req.set("destination_user", user2);

        await req.save(null, {useMasterKey: true});
        return "Friend Request Sent";
    }
    else{
        return "Friend Request already sent";
    }

}

async function blockUser(source_user, blocked_user){
    const block = Parse.Object.extend("Blocked");
    const blockQuery = new Parse.Query(block);
    blockQuery.equalTo("source_user", source_user);
    blockQuery.equalTo("blocked_user", blocked_user);

    remFriend1 = await removeFriend(source_user, blocked_user)
    remFriend2 = await removeFriend(blocked_user, source_user)

    remReq = await removeRequestUser(source_user, blocked_user);

    const results = await blockQuery.find({useMasterKey: true})
    if (results.length === 0){
        const b = new block();
        b.set("source_user", source_user);
        b.set("blocked_user", blocked_user);
        await b.save(null, {useMasterKey: true});
        return "user blocked";
    } else {
        return "User already blocked";
    }
}

async function unblockUser(source_user, blocked_user) {
    const block = Parse.Object.extend("Blocked");
    const blockQuery = new Parse.Query(block);
    blockQuery.equalTo("source_user", source_user);
    blockQuery.equalTo("blocked_user", blocked_user);

    const results = await blockQuery.find({useMasterKey: true});
    if (results.length > 0) {
        // If a block record exists, delete it to unblock the user
        for (let blockRecord of results) {
            await blockRecord.destroy({useMasterKey: true});
        }
        return "User unblocked";
    } else {
        return "No block record found";
    }
}

async function removeRequestUser(user1, user2){
    const FriendRequest = Parse.Object.extend("FriendRequest");
    const requestQuery = new Parse.Query(FriendRequest);

    requestQuery.equalTo("source_user", user1);
    requestQuery.equalTo("destination_user", user2);

    const requestQuery2 = new Parse.Query(FriendRequest);
    requestQuery2.equalTo("source_user", user2);
    requestQuery2.equalTo("destination_user", user1);

    const mainQuery = Parse.Query.or(requestQuery, requestQuery2);

    const requestResults = await mainQuery.find({useMasterKey: true});

    if (requestResults.length > 0) {
        for (let request of requestResults) {
            await request.destroy({useMasterKey: true});
        }
        return "Friend Request removed successfully.";
    } else {
        return "No Friend Request found to remove.";
    }
}

async function removeRequest(user1_id, user2_id){

    const FriendRequest = Parse.Object.extend("FriendRequest");
    const requestQuery = new Parse.Query(FriendRequest);

    user1 = await getUserById(user1_id);
    user2 = await getUserById(user2_id);

    requestQuery.equalTo("source_user", user1);
    requestQuery.equalTo("destination_user", user2);

    const requestQuery2 = new Parse.Query(FriendRequest);
    requestQuery2.equalTo("source_user", user2);
    requestQuery2.equalTo("destination_user", user1);

    const mainQuery = Parse.Query.or(requestQuery, requestQuery2);

    const requestResults = await mainQuery.find({useMasterKey: true});

    if (requestResults.length > 0) {
        for (let request of requestResults) {
            await request.destroy({useMasterKey: true});
        }
        return "Friend Request removed successfully.";
    } else {
        return "No Friend Request found to remove.";
    }

}

async function isFriend(user1, user2) {
    const query = user1.relation("friends").query();
    query.equalTo("objectId", user2.id);

    const results = await query.find({useMasterKey: true});

    return results.length > 0;
}

Parse.Cloud.define("checkFriend", async (request) => {
    const { userId1, userId2 } = request.params;

    try {
        // Fetch user objects by their IDs
        const user1 = await getUserById(userId1);
        const user2 = await getUserById(userId2);

        if (!user1) {
            return {error: "User1 not found"};
        }
        if (!user2) {g
            return {error: "User2 not found"};
        }

        // Call isFriend to check if user1 and user2 are friends
        const areFriends = await isFriend(user1, user2);
        return {isFriend: areFriends};
    } catch (error) {
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }
});

Parse.Cloud.define("blockUserById", async (request) => {
    const { currentUserId, friendUserId } = request.params;

    try{
        user1 = await getUserById(currentUserId);
        user2 = await getUserById(friendUserId);
        if (!user1) {
            return "Current User not found";
        }
        if (!user2) {
            return "Friend user not found.";
        }

        result = await blockUser(user1, user2);
        return result;

    } catch (error) {
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }

});

Parse.Cloud.define("suggestFriends", async (request) => {
    const userId = request.params.userId;
    const userQuery = new Parse.Query(Parse.User);
    userQuery.equalTo("objectId", userId);

    try {
        const user = await userQuery.first({ useMasterKey: true });
        if (!user) {
        return "User not found.";
        }


        const friends = await fetchFriends(user);
        const friendIds = friends.map(friend => friend.id).concat([userId]); // Include the user to exclude


        const potentialFriends = await FetchFriendsOfFriendsNonMutual(user);
        // return potentialFriends;
        const suggestedFriends = potentialFriends.map(detail => {
            const pf = detail.user;
            const profilePic = pf.get("profile_pic");
            const profilePicUrl = profilePic ? profilePic.url() : null;
            return {
                user: {
                    objectId: pf.id,
                    username: pf.get("username"),
                    profilePicUrl: profilePicUrl
                },
                mutualFriendsCount : detail.mutualFriendsCount
            };
        });

    } catch (error) {
        console.error("Error suggesting friends: ", error);
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, "Error suggesting friends.");
    }
});

Parse.Cloud.define("checkAndAddFriendByUsername", async (request) => {
    const { currentUserId, friendUsername } = request.params;

    try{
        const currentUser = await getUserById(currentUserId);
        const friendUser = await getUserByUsername(friendUsername);

        if (!currentUser) {
            return "Current User not found";
        }
        if (!friendUser) {
            return "Friend user not found.";
        }
        const result = await addFriend(currentUser, friendUser);
        return result;
    }
    catch (error) {
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }

});

Parse.Cloud.define("checkAndAddFriendById", async (request) => {
    const { currentUserId, friendUserId } = request.params;

    try{
        const currentUser = await getUserById(currentUserId);
        const friendUser = await getUserById(friendUserId);

        if (!currentUser) {
            return "Current User not found";
        }
        if (!friendUser) {
            return "Friend user not found.";
        }
        const result = await addFriend(currentUser, friendUser);
        const result2 = await addFriend(friendUser, currentUser);
        return result;
    }
    catch (error) {
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }

});

Parse.Cloud.define("checkAndRemoveFriendById", async (request) => {
    const { currentUserId, friendUserId } = request.params;

    try {
        const currentUser = await getUserById(currentUserId);
        const friendUser = await getUserById(friendUserId);

        if (!currentUser) {
            return "Current User not found";
        }
        if (!friendUser) {
            return "Friend user not found.";
        }

        // Attempt to remove friendUser from currentUser's friends
        const result = await removeFriend(currentUser, friendUser);
        // Attempt to remove currentUser from friendUser's friends
        const result2 = await removeFriend(friendUser, currentUser);


        // You might want to adjust what you return based on your application's needs
        return result;
    } catch (error) {
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }
});

Parse.Cloud.define("friendRequestByUsername", async (request) => {
    const {currentUserId, friendUsername} = request.params;

    try {
        const currentUser = await getUserById(currentUserId);
        const friendUser = await getUserByUsername(friendUsername);

        if (!currentUser) {
            return "Current User not found";
        }
        if (!friendUser) {
            return "Friend user not found.";
        }
        const result = await addFriendRequest(currentUser, friendUser);
        return result;
    }
    catch(error){
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }
});

Parse.Cloud.define("friendRequestByUserId", async (request) => {
    const { currentUserId, friendUserId } = request.params;

    try {
        const currentUser = await getUserById(currentUserId);
        const friendUser = await getUserById(friendUserId);

        if (!currentUser) {
            return "Current User not found";
        }
        if (!friendUser) {
            return "Friend user not found.";
        }
        const result = await addFriendRequest(currentUser, friendUser);
        return result;
    }
    catch(error){
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }
});

Parse.Cloud.define("suggestFriends2", async (request) => {
    const userId = request.params.userId;

    try{
        const user = await getUserById(userId)

        const potentialFriends = await FetchFriendsOfFriends2(user, userId);
        return potentialFriends;
    } catch(error){
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, error.message);
    }
});


Parse.Cloud.define("suggestFriendsTest", async (request) =>{
    const userId = request.params.userId;
    const userQuery = new Parse.Query(Parse.User);
    userQuery.equalTo("objectId", userId);

    try {
        const user = await userQuery.first({ useMasterKey: true });
        if (!user) {
            return "User not found.";
        }


        const friends = await fetchFriends(user);
        const friendIds = friends.map(friend => friend.id).concat([userId]); // Include the user to exclude


        const potentialFriends = await FetchFriendsOfFriendsNonMutual(user);
        return potentialFriends;
        // return potentialFriends;
        // const suggestedFriends = potentialFriends.map(detail => {
        //     const pf = detail.user;
        //     const profilePic = pf.get("profile_pic");
        //     const profilePicUrl = profilePic ? profilePic.url() : null;
        //     return {
        //         user: {
        //             objectId: pf.id,
        //             username: pf.get("username"),
        //             profilePicUrl: profilePicUrl
        //         },
        //         mutualFriendsCount : detail.mutualFriendsCount
        //     };
        // });

    } catch (error) {
        console.error("Error suggesting friends: ", error);
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, "Error suggesting friends.");
    }
});

Parse.Cloud.define("requestedFriends", async (request) => {
    const userId = request.params.userId;
    const user = await getUserById(userId);

    const results = await fetchRequestedFriends(user);
    return results;

});

Parse.Cloud.define("getBlockedUsers", async (request) => {
    const userId = request.params.userId;
    const user = await getUserById(userId);



    const results = await fetchBlockedUsers(user);
    return results;
});

Parse.Cloud.define("getBlockedAndBlockedBy", async (request) => {
    const userId = request.params.userId;

    const user = await getUserById(userId);

    const allBlockedIds = new Set();

    const blockedByUser = await fetchBlocked(user);
    blockedByUser.forEach(id => allBlockedIds.add(id));

    const blockingUser = await fetchBlocker(user);
    blockingUser.forEach(id => allBlockedIds.add(id));

    return Array.from(allBlockedIds);

});

Parse.Cloud.define("unblock", async (request) => {
    const {currentUserId, friendUserId} = request.params;

    const user1 = await getUserById(currentUserId);
    const user2 = await getUserById(friendUserId);

    const result = await unblockUser(user1, user2);
    return result;
});

Parse.Cloud.define("addVote", async (request) => {
    const {currentUserId, reviewId, vote} = request.params;


    const Review = Parse.Object.extend("Review");
    const reviewQuery = new Parse.Query(Review);
    reviewQuery.equalTo("objectId", reviewId);
    const review = await reviewQuery.first({useMasterKey: true});

    if(!review){
        throw new Error("Review not found");
    }

    let upVoters = review.get("upVoters") || [];
    let downVoters = review.get("downVoters") || [];

    const hasVotedUp = upVoters.includes(currentUserId);
    const hasVotedDown = downVoters.includes(currentUserId);

    if (hasVotedUp || hasVotedDown) {
        if (hasVotedUp) {
            review.remove("upVoters", currentUserId);
            review.increment("upvotes", -1);
        }
        if (hasVotedDown) {
            review.remove("downVoters", currentUserId);
            review.increment("downvotes", -1);
        }

        if ((hasVotedUp && vote === "up") || (hasVotedDown && vote === "down")) {
            await review.save(null, { useMasterKey: true });
            return {
                message: "Already Voted",
                upvotes: review.get("upvotes"),
                downvotes: review.get("downvotes")
            };
        }
    }
    if (vote === "up") {
        review.addUnique("upVoters", currentUserId);
        review.increment("upvotes");
    } else if (vote === "down") {
        review.addUnique("downVoters", currentUserId);
        review.increment("downvotes");
    }
    await review.save(null, { useMasterKey: true });
    return {
          message: "Vote Saved",
          upvotes: review.get("upvotes"),
          downvotes: review.get("downvotes")
    };
});

Parse.Cloud.define("searchUsers", async (request) => {
    const {currentUserId, keyword} = request.params;
    if (!keyword) {
        throw new Error("Keyword must be provided.");
    }
    if (!currentUserId) {
        throw new Error("User ID must be provided.");
    }

    try {
        const user = await getUserById(currentUserId);

        const blockedByQuery = new Parse.Query("Blocked");
        blockedByQuery.equalTo("source_user", user);
        blockedByQuery.select("blocked_user");

        const blockedQuery = new Parse.Query("Blocked");
        blockedQuery.equalTo("blocked_user", user);
        blockedQuery.select("source_user");

        const blockedByResults = await blockedByQuery.find({useMasterKey:true});
        const blockedResults = await blockedQuery.find({useMasterKey:true});

        let blockedUserIds = [];
        blockedByResults.forEach((item) => blockedUserIds.push(item.get("blocked_user").id));
        blockedResults.forEach((item) => blockedUserIds.push(item.get("source_user").id));

        const userQuery = new Parse.Query(Parse.User);
        userQuery.startsWith("username", keyword);
        userQuery.notContainedIn("objectId", blockedUserIds);
        userQuery.notEqualTo("objectId", currentUserId);

        const searchResults = await userQuery.find({useMasterKey: true});

        let uDetails = [];
        for (detail of searchResults){

            const mutFriendCount = await countMutualFriends(user, detail);
            uDetails.push({user: detail, mutualFriendsCount: mutFriendCount});
        }
        return uDetails;





    } catch (error) {
        console.error("Error searching for users: ", error);
        throw new Parse.Error(Parse.Error.SCRIPT_FAILED, "Error searching for users.");
    }
});

Parse.Cloud.define("deleteFriendRequest", async (request) => {
    const {currentUserId, friendUserId} = request.params;

    const result = await removeRequest(currentUserId, friendUserId);
    return result;

});

